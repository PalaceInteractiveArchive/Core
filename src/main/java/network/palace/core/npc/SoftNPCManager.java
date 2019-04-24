package network.palace.core.npc;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import lombok.Getter;
import network.palace.core.Core;
import network.palace.core.events.CorePlayerJoinedEvent;
import network.palace.core.npc.mob.MobPlayer;
import network.palace.core.packets.server.entity.WrapperPlayServerPlayerInfo;
import network.palace.core.packets.server.scoreboard.WrapperPlayServerScoreboardTeam;
import network.palace.core.player.CPlayer;
import network.palace.core.utils.MiscUtil;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.util.Vector;

import java.lang.ref.WeakReference;
import java.util.*;

public final class SoftNPCManager implements Listener {
    private static final String HIDDEN_TEAM = "hidden_players";
    private static final int RENDER_DISTANCE = 60;
    private static final int TELEPORT_MIN_DISTANCE = 15;
    @Getter private IDManager iDManager;
    @Getter private final Set<WeakReference<AbstractEntity>> entityRefs = new HashSet<>();
    private List<String> hiddenPlayerMobs = new ArrayList<>();
    private HashMap<UUID, List<MobPlayer>> removeFromTabList = new HashMap<>();

    public SoftNPCManager() {
        iDManager = new IDManager();
        Core.registerListener(this);
        Core.runTaskTimer(() -> {
            HashMap<UUID, List<MobPlayer>> localMap = (HashMap<UUID, List<MobPlayer>>) removeFromTabList.clone();
            removeFromTabList.clear();
            for (Map.Entry<UUID, List<MobPlayer>> entry : localMap.entrySet()) {
                UUID uuid = entry.getKey();
                List<MobPlayer> mobs = entry.getValue();
                CPlayer player = Core.getPlayerManager().getPlayer(uuid);
                if (player == null) continue;
                if (player.getOnlineTime() < 3000) {
                    removeFromTabList.put(uuid, mobs);
                    continue;
                }

                for (MobPlayer mob : mobs) {
                    if (mob == null) continue;
                    WrapperPlayServerPlayerInfo hideName = new WrapperPlayServerPlayerInfo();
                    hideName.setAction(EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
                    hideName.setData(Collections.singletonList(new PlayerInfoData(new WrappedGameProfile(mob.getUuid(), mob.getCustomName()), 0, EnumWrappers.NativeGameMode.ADVENTURE, null)));
                    hideName.sendPacket(player);
                }
            }
        }, 20L, 10L);
    }

    private void ensureAllValid() {
        entityRefs.removeIf(mob -> mob.get() == null);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerMove(PlayerMoveEvent event) {
        CPlayer player = Core.getPlayerManager().getPlayer(event.getPlayer());
        if (player == null) return;
        Location from = event.getFrom().clone();
        Location to = event.getTo().clone();
        to.setYaw(to.getYaw() % 360);
        updatePosition(player, from, to);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        CPlayer player = Core.getPlayerManager().getPlayer(event.getPlayer());
        if (player == null) return;
        Location from = event.getFrom().clone();
        Location to = event.getTo().clone();
        updatePosition(player, from, to);
    }

    private void updatePosition(CPlayer player, Location from, Location to) {
        boolean changedView = from.getYaw() != to.getYaw() || from.getPitch() != to.getPitch();
        boolean changedPosition = from.getBlockX() != to.getBlockX() || from.getBlockY() != to.getBlockY() || from.getBlockZ() != to.getBlockZ();
        updateMobs(player, to, changedPosition, changedView);
    }

    @EventHandler
    public void onPlayerJoin(CorePlayerJoinedEvent event) {
        ensureAllValid();
        CPlayer player = event.getPlayer();
        //Create team for hidden players
        WrapperPlayServerScoreboardTeam wrapper = new WrapperPlayServerScoreboardTeam();
        wrapper.setMode(WrapperPlayServerScoreboardTeam.Mode.TEAM_CREATED);
        wrapper.setName(HIDDEN_TEAM);
        wrapper.setNameTagVisibility("never");
        wrapper.setPlayers(hiddenPlayerMobs);
        wrapper.sendPacket(player);
        updateMobs(player, null, true, false);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        ensureAllValid();
        playerLogout(Core.getPlayerManager().getPlayer(event.getPlayer()));
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        ensureAllValid();
        playerLogout(Core.getPlayerManager().getPlayer(event.getPlayer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        ensureAllValid();
        updateMobs(Core.getPlayerManager().getPlayer(event.getPlayer()), null, true, false);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerWorldChange(PlayerChangedWorldEvent event) {
        ensureAllValid();
        CPlayer player = Core.getPlayerManager().getPlayer(event.getPlayer());
        for (WeakReference<AbstractEntity> mobRef : entityRefs) {
            AbstractEntity mobNPC = mobRef.get();
            if (mobNPC == null || mobNPC.getVisibleTo().size() != 0 && MiscUtil.contains(mobNPC.getTargets(), event.getPlayer()))
                continue;
            if (mobNPC.getLocation().getWorld() == null) {
                if (event.getPlayer().getWorld().equals(mobNPC.getLocation().getWorld())) {
                    mobNPC.forceSpawn(player);
                } else {
                    mobNPC.forceDespawn(player);
                }
            }
        }
    }

    private void updateMobs(CPlayer player, Location loc, boolean spawn, boolean tabList) {
        if (loc == null) {
            loc = player.getLocation();
        }
        for (WeakReference<AbstractEntity> mobRef : entityRefs) {
            final AbstractEntity npcMob = mobRef.get();
            if (npcMob == null) continue;
            if (npcMob.isSpawned() && npcMob.canSee(player) && npcMob.sameWorld(player)) {
                if (spawn) {
                    boolean viewer = npcMob.isViewer(player);
                    double distance = loc.distance(npcMob.getLocation().getLocation());
                    if (distance <= RENDER_DISTANCE && !viewer) {
                        npcMob.forceSpawn(player);
                    } else if (distance > RENDER_DISTANCE && viewer) {
                        npcMob.forceDespawn(player);
                    }
                }
                if (!tabList || !npcMob.getEntityType().equals(EntityType.PLAYER) || !npcMob.isViewer(player))
                    continue;
                MobPlayer mobPlayer = (MobPlayer) npcMob;

                if (!mobPlayer.needsRemoveFromTabList(player)) continue;

                Vector mobLoc = mobPlayer.getLocation().getLocation().toVector();

                Location copy = loc.clone();
                copy.setDirection(mobLoc.subtract(copy.toVector()));
                float yaw = copy.getYaw();
                float playerYaw = loc.getYaw();
                if (yaw < 0) yaw += 360;
                if (playerYaw < 0) playerYaw += 360;
                float difference = Math.abs(playerYaw - yaw);
                if (difference <= 60) {
                    removeFromTabList(player, mobPlayer);
                }
            }
        }
    }

    private void playerLogout(CPlayer player) {
        for (WeakReference<AbstractEntity> mobRef : entityRefs) {
            final AbstractEntity npcMob = mobRef.get();
            if (npcMob != null && npcMob.isSpawned() && npcMob.isViewer(player)) {
                npcMob.removeViewer(player);
            }
        }
    }

    public void trackHiddenPlayerMob(MobPlayer mob) {
        hiddenPlayerMobs.add(mob.getCustomName());

        WrapperPlayServerScoreboardTeam wrapper = new WrapperPlayServerScoreboardTeam();
        wrapper.setName(HIDDEN_TEAM);
        wrapper.setMode(WrapperPlayServerScoreboardTeam.Mode.PLAYERS_ADDED);
        wrapper.setPlayers(Collections.singletonList(mob.getCustomName()));

        Arrays.asList(mob.getTargets()).forEach(wrapper::sendPacket);
    }

    public void untrackHiddenPlayerMob(MobPlayer mob) {
        hiddenPlayerMobs.remove(mob.getCustomName());

        WrapperPlayServerScoreboardTeam wrapper = new WrapperPlayServerScoreboardTeam();
        wrapper.setName(HIDDEN_TEAM);
        wrapper.setMode(WrapperPlayServerScoreboardTeam.Mode.PLAYERS_REMOVED);
        wrapper.setPlayers(Collections.singletonList(mob.getCustomName()));

        Arrays.asList(mob.getTargets()).forEach(wrapper::sendPacket);
    }

    public void removeFromTabList(CPlayer player, MobPlayer mob) {
        mob.removedFromTabList(player.getUniqueId());
        List<MobPlayer> list;
        if (removeFromTabList.containsKey(player.getUniqueId())) {
            list = removeFromTabList.get(player.getUniqueId());
        } else {
            list = new ArrayList<>();
        }
        list.add(mob);
        removeFromTabList.put(player.getUniqueId(), list);
    }
}

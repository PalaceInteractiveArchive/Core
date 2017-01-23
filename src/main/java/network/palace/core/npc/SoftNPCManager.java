package network.palace.core.npc;

import lombok.Getter;
import network.palace.core.Core;
import network.palace.core.packets.server.entity.WrapperPlayServerEntityDestroy;
import network.palace.core.pathfinding.Point;
import network.palace.core.player.CPlayer;
import network.palace.core.utils.MiscUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public final class SoftNPCManager implements Listener {

    @Getter private final Set<WeakReference<AbstractMob>> mobRefs = new HashSet<>();

    public SoftNPCManager() {
        Core.registerListener(this);
    }

    private void ensureAllValid() {
        mobRefs.removeIf(mob -> mob.get() == null);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        ensureAllValid();
        CPlayer player = Core.getPlayerManager().getPlayer(event.getPlayer());
        for (WeakReference<AbstractMob> mobRef : mobRefs) {
            final AbstractMob npcMob = mobRef.get();
            if (npcMob == null) continue;
            if (npcMob.isSpawned() && npcMob.getViewers().size() == 0) {
                npcMob.forceSpawn(player);
            }
        }
    }

    // TODO REMOVE THIS BAD METHOD. Need to create own method for checking player to pet and everything or if will update position for everyones movement and derp.
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerMove(PlayerMoveEvent event) {
        ensureAllValid();
        CPlayer player = Core.getPlayerManager().getPlayer(event.getPlayer());
        for (WeakReference<AbstractMob> mobRef : mobRefs) {
            final AbstractMob npcMob = mobRef.get();
            if (npcMob == null) continue;
            if (!npcMob.isSpawned()) continue;
            Point point = Point.of(player.getLocation());
            npcMob.move(point);
        }
    }

    @EventHandler
    public void onPlayerDisconnect(CPlayer player) {
        ensureAllValid();
        for (WeakReference<AbstractMob> mobRef : mobRefs) {
            final AbstractMob npcMob = mobRef.get();
            if (npcMob == null) continue;
            if (npcMob.getViewers().contains(player)) npcMob.removeViewer(player);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        ensureAllValid();
        CPlayer player = Core.getPlayerManager().getPlayer(event.getPlayer());
        for (WeakReference<AbstractMob> mobRef : mobRefs) {
            final AbstractMob npcMob = mobRef.get();
            if (npcMob == null) continue;
            if (!npcMob.isSpawned()) continue;
            if (!npcMob.getWorld().equals(event.getRespawnLocation().getWorld())) continue;
            Core.runTask(() -> npcMob.forceSpawn(player));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerWorldChange(PlayerChangedWorldEvent event) {
        ensureAllValid();
        CPlayer player = Core.getPlayerManager().getPlayer(event.getPlayer());
        for (WeakReference<AbstractMob> mobRef : mobRefs) {
            AbstractMob mobNPC = mobRef.get();
            if (mobNPC == null || mobNPC.getViewers().size() != 0 && MiscUtil.contains(mobNPC.getTargets(), event.getPlayer())) continue;
            if (mobNPC.getWorld() == null || event.getPlayer().getWorld().equals(mobNPC.getWorld())) mobNPC.forceSpawn(player);
        }
    }

    public void removeAllEntities() {
        ensureAllValid();
        LinkedHashSet<Integer> ids = new LinkedHashSet<>();
        for (WeakReference<AbstractMob> villagerRef : mobRefs) {
            AbstractMob abstractMobNPC = villagerRef.get();
            if (abstractMobNPC == null) continue;
            ids.add(abstractMobNPC.getId());
            abstractMobNPC.spawned = false;
        }
        WrapperPlayServerEntityDestroy packet = new WrapperPlayServerEntityDestroy();
        int[] idsArray = new int[ids.size()];
        Integer[] idsIntegerArray = ids.toArray(new Integer[ids.size()]);
        for (int x = 0; x < ids.size(); x++) {
            idsArray[x] = idsIntegerArray[x];
        }
        packet.setEntityIds(idsArray);
        for (CPlayer player : Core.getPlayerManager().getOnlinePlayers()) {
            packet.sendPacket(player);
        }
    }
}

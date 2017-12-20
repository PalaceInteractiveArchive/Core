package network.palace.core.player.impl.managers;

import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import network.palace.core.Core;
import network.palace.core.dashboard.packets.dashboard.PacketConfirmPlayer;
import network.palace.core.dashboard.packets.dashboard.PacketGetPack;
import network.palace.core.events.CorePlayerJoinedEvent;
import network.palace.core.player.CPlayer;
import network.palace.core.player.CPlayerManager;
import network.palace.core.player.PlayerStatus;
import network.palace.core.player.Rank;
import network.palace.core.player.impl.CorePlayer;
import network.palace.core.player.impl.CorePlayerDefaultScoreboard;
import network.palace.core.player.impl.listeners.CorePlayerManagerListener;
import network.palace.core.player.impl.listeners.CorePlayerStaffLoginListener;
import network.palace.core.utils.SqlUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * The type Core player manager.
 */
public class CorePlayerManager implements CPlayerManager {

    private final CorePlayerDefaultScoreboard defaultScoreboard;
    private final Map<UUID, CPlayer> onlinePlayers = new HashMap<>();

    /**
     * Instantiates a new Core player manager.
     */
    public CorePlayerManager() {
        Core.registerListener(new CorePlayerManagerListener());
        Core.registerListener(new CorePlayerStaffLoginListener());
        defaultScoreboard = new CorePlayerDefaultScoreboard();
    }

    @Override
    public void playerLoggedIn(UUID uuid, String name) {
//        System.out.println(System.currentTimeMillis() + " a");
        SqlUtil.JoinReport report = Core.getSqlUtil().getJoinReport(uuid);
//        System.out.println(System.currentTimeMillis() + " b");
        onlinePlayers.put(uuid, new CorePlayer(report.getSqlId(), uuid, name, report.getRank()));
//        System.out.println(System.currentTimeMillis() + " c");
    }

    @Override
    public void playerJoined(Player player) {
        // Player skins
        WrappedGameProfile wrappedGameProfile = WrappedGameProfile.fromPlayer(player);
        String textureValue = "";
        String textureSignature = "";
        Optional<WrappedSignedProperty> propertyOptional = wrappedGameProfile.getProperties().get("textures").stream().findFirst();
        if (propertyOptional.isPresent()) {
            WrappedSignedProperty property = propertyOptional.get();
            textureValue = property.getValue();
            textureSignature = property.getSignature();
        }
        // Get core player
        CPlayer corePlayer = getPlayer(player);
        if (corePlayer == null) return;
        // Joined
        corePlayer.setStatus(PlayerStatus.JOINED);
        // Set skin info
        corePlayer.setTextureValue(textureValue);
        corePlayer.setTextureSignature(textureSignature);
        if (corePlayer.getRank().getRankId() >= Rank.CHARACTER.getRankId()) {
            Core.runTaskAsynchronously(() ->
                    Core.getSqlUtil().cacheSkin(corePlayer.getUuid(), corePlayer.getTextureValue(), corePlayer.getTextureSignature())
            );
        }
        // Setup permissions for player
        Core.getPermissionManager().login(corePlayer);
        // Achievements Task
        Core.runTaskAsynchronously(() -> {
            List<Integer> ids = Core.getSqlUtil().getAchievements(corePlayer.getUniqueId());
            corePlayer.setAchievementManager(new CorePlayerAchievementManager(corePlayer, ids));
            Core.getCraftingMenu().update(corePlayer, 2, Core.getCraftingMenu().getAchievement(corePlayer));
            corePlayer.setHonor(Core.getSqlUtil().getHonor(corePlayer.getSqlId()));
            corePlayer.setPreviousHonorLevel(Core.getHonorManager().getLevel(corePlayer.getHonor()).getLevel());
            Core.getHonorManager().displayHonor(corePlayer, true);
        });
        // Packets
        Core.getDashboardConnection().send(new PacketGetPack(corePlayer.getUniqueId(), ""));
        Core.getDashboardConnection().send(new PacketConfirmPlayer(corePlayer.getUniqueId(), false));
        // Display the scoreboard
        displayRank(corePlayer);
        // Tab header and footer
        corePlayer.getHeaderFooter().setHeaderFooter(Core.getInstance().getTabHeader(), Core.getInstance().getTabFooter());
        // Show the title if we're supposed to
        if (Core.getInstance().isShowTitleOnLogin()) {
            player.sendTitle(Core.getInstance().getLoginTitle(), Core.getInstance().getLoginSubTitle(),
                    Core.getInstance().getLoginTitleFadeIn(), Core.getInstance().getLoginTitleStay(), Core.getInstance().getLoginTitleFadeOut());
        }
        // Called joined event
        new CorePlayerJoinedEvent(corePlayer).call();
    }

    @Override
    public void playerLoggedOut(Player player) {
        if (player == null) return;
        removePlayer(player.getUniqueId());
    }

    @Override
    public void removePlayer(UUID uuid) {
        if (uuid == null) return;
        CPlayer cPlayer = getPlayer(uuid);
        if (cPlayer == null) return;
        Core.runTask(() -> {
            for (CPlayer otherPlayer : Core.getPlayerManager().getOnlinePlayers()) {
                cPlayer.getScoreboard().removePlayerTag(otherPlayer);
                otherPlayer.getScoreboard().removePlayerTag(cPlayer);
            }
        });
        cPlayer.resetManagers();
        cPlayer.setStatus(PlayerStatus.LEFT);
        onlinePlayers.remove(cPlayer.getUniqueId());
    }

    @Override
    public void broadcastMessage(String message) {
        getOnlinePlayers().forEach(player -> player.sendMessage(message));
    }

    @Override
    public CPlayer getPlayer(UUID playerUUID) {
        return onlinePlayers.get(playerUUID);
    }

    @Override
    public CPlayer getPlayer(Player player) {
        if (player == null) {
            return null;
        }
        return getPlayer(player.getUniqueId());
    }

    @Override
    public CPlayer getPlayer(String name) {
        Player p = Bukkit.getPlayer(name);
        if (p == null)
            return null;
        return getPlayer(p);
    }

    @Override
    public CPlayer getPlayer(int sqlId) {
        for (CPlayer p : getOnlinePlayers()) {
            if (p.getSqlId() == sqlId) {
                return p;
            }
        }
        return null;
    }

    @Override
    public List<CPlayer> getOnlinePlayers() {
        if (onlinePlayers == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(onlinePlayers.values());
    }

    @Override
    public void displayRank(CPlayer player) {
        // Set op if the player should be
        boolean op = player.getRank().isOp();
        if (player.isOp() != op) {
            player.setOp(op);
        }

        player.getScoreboard().setupPlayerTags();
        for (CPlayer otherPlayer : Core.getPlayerManager().getOnlinePlayers()) {
            if (player.getScoreboard() != null) player.getScoreboard().addPlayerTag(otherPlayer);
            if (otherPlayer.getScoreboard() != null) otherPlayer.getScoreboard().addPlayerTag(player);
        }
        defaultScoreboard.setup(player);
    }
}

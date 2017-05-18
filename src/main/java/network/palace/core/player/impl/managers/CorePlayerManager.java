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
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * The type Core player manager.
 */
public class CorePlayerManager implements CPlayerManager {

    private CorePlayerDefaultScoreboard defaultScoreboard;
    private final HashMap<UUID, CPlayer> onlinePlayers = new HashMap<>();

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
        Rank rank = Core.getSqlUtil().getRank(uuid);
        List<Integer> ids = Core.getSqlUtil().getAchievements(uuid);
        onlinePlayers.put(uuid, new CorePlayer(uuid, name, rank, ids));
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
        CPlayer cPlayer = getPlayer(player);
        if (cPlayer == null) return;
        // Joined
        cPlayer.setStatus(PlayerStatus.JOINED);
        // Set skin info
        cPlayer.setTextureValue(textureValue);
        cPlayer.setTextureSignature(textureSignature);
        if (cPlayer.getRank().getRankId() >= Rank.CHARACTER.getRankId()) {
            Core.runTaskAsynchronously(() ->
                    Core.getSqlUtil().cacheSkin(cPlayer.getUuid(), cPlayer.getTextureValue(), cPlayer.getTextureSignature())
            );
        }
        // Set op if they can be
        boolean op = cPlayer.getRank().isOp();
        if (cPlayer.isOp() != op) {
            cPlayer.setOp(op);
        }
        // Setup permissions for player
        Core.getPermissionManager().login(cPlayer);
        // Packets
        Core.getDashboardConnection().send(new PacketGetPack(cPlayer.getUniqueId(), ""));
        Core.getDashboardConnection().send(new PacketConfirmPlayer(cPlayer.getUniqueId(), false));
        // Scoreboard
        if (cPlayer.getScoreboard() != null) cPlayer.getScoreboard().setupPlayerTags();
        for (CPlayer otherPlayer : Core.getPlayerManager().getOnlinePlayers()) {
            if (cPlayer.getScoreboard() != null) cPlayer.getScoreboard().addPlayerTag(otherPlayer);
            if (otherPlayer.getScoreboard() != null) otherPlayer.getScoreboard().addPlayerTag(cPlayer);
        }
        defaultScoreboard.setup(cPlayer);
        // Tab header and footer
        cPlayer.getHeaderFooter().setHeaderFooter(Core.getInstance().getTabHeader(), Core.getInstance().getTabFooter());
        // Show the title if we're supposed to
        if (Core.getInstance().isShowTitleOnLogin()) {
            player.sendTitle(Core.getInstance().getLoginTitle(), Core.getInstance().getLoginSubTitle(),
                    Core.getInstance().getLoginTitleFadeIn(), Core.getInstance().getLoginTitleStay(), Core.getInstance().getLoginTitleFadeOut());
        }
        // Called joined event
        new CorePlayerJoinedEvent(cPlayer).call();
    }

    @Override
    public void playerLoggedOut(Player player) {
        if (getPlayer(player) == null) return;
        CPlayer cPlayer = getPlayer(player);
        for (CPlayer otherPlayer : Core.getPlayerManager().getOnlinePlayers()) {
            cPlayer.getScoreboard().removePlayerTag(otherPlayer);
            otherPlayer.getScoreboard().removePlayerTag(cPlayer);
        }
        cPlayer.resetManagers();
        cPlayer.setStatus(PlayerStatus.LEFT);
        onlinePlayers.remove(cPlayer.getUniqueId());
    }

    @Override
    public void broadcastMessage(String message) {
        getOnlinePlayers().forEach(player -> player.sendMessage(message));
    }

    @Override
    public CorePlayer getPlayer(UUID playerUUID) {
        return (CorePlayer) onlinePlayers.get(playerUUID);
    }

    @Override
    public CorePlayer getPlayer(Player player) {
        return getPlayer(player.getUniqueId());
    }

    @Override
    public CPlayer getPlayer(String name) {
        return getPlayer(Bukkit.getPlayer(name));
    }

    @Override
    public List<CPlayer> getOnlinePlayers() {
        return new ArrayList<>(onlinePlayers.values());
    }
}

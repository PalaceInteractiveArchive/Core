package network.palace.core.player.impl.managers;

import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.core.player.CPlayerManager;
import network.palace.core.player.PlayerStatus;
import network.palace.core.player.Rank;
import network.palace.core.player.impl.CorePlayer;
import network.palace.core.player.impl.listeners.CorePlayerManagerListener;
import network.palace.core.player.impl.listeners.CorePlayerStaffLoginListener;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * The type Core player manager.
 */
public class CorePlayerManager implements CPlayerManager {

    private final HashMap<UUID, CPlayer> onlinePlayers = new HashMap<>();

    /**
     * Instantiates a new Core player manager.
     */
    public CorePlayerManager() {
        Core.registerListener(new CorePlayerManagerListener());
        Core.registerListener(new CorePlayerStaffLoginListener());
    }

    @Override
    public void playerLoggedIn(UUID uuid, String name) {
        playerLoggedOut(uuid);
        Rank rank = Core.getSqlUtil().getRank(uuid);
        List<Integer> ids = Core.getSqlUtil().getAchievements(uuid);
        onlinePlayers.put(uuid, new CorePlayer(uuid, name, rank, ids));
    }

    @Override
    public void playerJoined(UUID uuid, String textureValue, String textureSignature) {
        CPlayer player = getPlayer(uuid);
        if (player == null) return;
        player.setStatus(PlayerStatus.JOINED);
        player.setTextureValue(textureValue);
        player.setTextureSignature(textureSignature);
        if (player.getRank().getRankId() >= Rank.CHARACTER.getRankId()) {
            Core.runTaskAsynchronously(() ->
                Core.getSqlUtil().cacheSkin(player.getUuid(), player.getTextureValue(), player.getTextureSignature())
            );
        }
        boolean op = player.getRank().isOp();
        if (player.isOp() != op) {
            player.setOp(op);
        }
        // Setup permissions for player
        Core.getPermissionManager().login(player);
    }
    @Override
    public void playerLoggedOut(UUID uuid) {
        if (getPlayer(uuid) == null) return;
        getPlayer(uuid).resetManagers();
        getPlayer(uuid).setStatus(PlayerStatus.LEFT);
        onlinePlayers.remove(uuid);
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
    public List<CPlayer> getOnlinePlayers() {
        return new ArrayList<>(onlinePlayers.values());
    }
}

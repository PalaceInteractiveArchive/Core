package network.palace.core.player.impl;

import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.core.player.CPlayerManager;
import network.palace.core.player.Rank;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

public class CorePlayerManager implements CPlayerManager {

    private final HashMap<UUID, CPlayer> onlinePlayers = new HashMap<>();

    public CorePlayerManager() {
        Core.registerListener(new CorePlayerManagerListener());
    }

    @Override
    public void playerLoggedIn(UUID uuid, String name) {
        playerLoggedOut(uuid);
        Rank rank = Core.getSqlUtil().getRank(uuid);
        onlinePlayers.put(uuid, new CorePlayer(uuid, name, rank));
    }

    @Override
    public void playerJoined(UUID uuid, String textureHash) {
        CPlayer player = getPlayer(uuid);
        if (player == null) return;
        player.setStatus(CPlayer.PlayerStatus.JOINED);
        player.setTextureHash(textureHash);
        //Setup permissions for player
        Core.getInstance().getPermissionManager().login(player);
    }

    @Override
    public void playerLoggedOut(UUID uuid) {
        if (getPlayer(uuid) == null) return;
        getPlayer(uuid).resetManagers();
        getPlayer(uuid).setStatus(CPlayer.PlayerStatus.LEFT);
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
    public Collection<CPlayer> getOnlinePlayers() {
        return onlinePlayers.values();
    }

    @Override
    public Iterator<CPlayer> iterator() {
        return getOnlinePlayers().iterator();
    }
}

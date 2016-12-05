package com.thepalace.core.player.impl;

import com.thepalace.core.Core;
import com.thepalace.core.player.CPlayer;
import com.thepalace.core.player.CPlayerManager;
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
        onlinePlayers.put(uuid, new CorePlayer(uuid, name));
    }

    @Override
    public void playerJoined(UUID uuid) {
        if (getPlayer(uuid) == null) return;
        getPlayer(uuid).setStatus(CPlayer.PlayerStatus.JOINED);
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

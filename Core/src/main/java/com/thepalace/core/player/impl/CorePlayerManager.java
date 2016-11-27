package com.thepalace.core.player.impl;

import com.thepalace.core.Core;
import com.thepalace.core.player.CPlayer;
import com.thepalace.core.player.CPlayerManager;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CorePlayerManager implements CPlayerManager {

    private final Map<UUID, CPlayer> onlinePlayers = new ConcurrentHashMap<>();

    public CorePlayerManager() {
        Core.registerListener(new CorePlayerManagerListener());
    }

    @Override
    public void playerLoggedIn(UUID uuid) {
        onlinePlayers.put(uuid, new CorePlayer(uuid));
    }

    @Override
    public void playerLoggedOut(UUID uuid) {
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

package com.thepalace.core.player.impl;

import com.thepalace.core.Core;
import com.thepalace.core.player.CPlayer;
import com.thepalace.core.player.CPlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CorePlayerManager implements CPlayerManager {

    private final Map<String, CPlayer> onlinePlayers = new ConcurrentHashMap<>();

    public CorePlayerManager() {
        Core.registerListener(new CorePlayerManagerListener());
    }

    @Override
    public void playerLoggedIn(Player player) {
        onlinePlayers.put(player.getName(), new CorePlayer(player.getUniqueId()));
    }

    @Override
    public void playerLoggedOut(Player player) {
        onlinePlayers.remove(player.getName());
    }

    @Override
    public void broadcastMessage(String message) {
        getOnlinePlayers().forEach(player -> player.sendMessage(message));
    }

    @Override
    public CorePlayer getPlayer(Player player) {
        return (CorePlayer) onlinePlayers.get(player.getName());
    }

    @Override
    public CPlayer getPlayer(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) return null;
        return getPlayer(player);
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

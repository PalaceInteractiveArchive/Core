package com.palacemc.core.player.impl;

import com.palacemc.core.Core;
import com.palacemc.core.player.CPlayer;
import com.palacemc.core.player.CPlayerManager;
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
        synchronized (onlinePlayers) {
            onlinePlayers.put(player.getName(), new CorePlayer(player.getUniqueId()));
        }
    }

    @Override
    public void playerLoggedOut(Player player) {
        synchronized (onlinePlayers) {
            onlinePlayers.remove(player.getName());
        }
    }

    @Override
    public CorePlayer getPlayer(Player player) {
        synchronized (onlinePlayers) {
            return (CorePlayer) onlinePlayers.get(player.getName());
        }
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

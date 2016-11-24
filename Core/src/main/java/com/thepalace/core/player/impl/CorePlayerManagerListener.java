package com.thepalace.core.player.impl;

import com.thepalace.core.Core;
import com.thepalace.core.events.CorePlayerJoinDelayedEvent;
import com.thepalace.core.player.CPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class CorePlayerManagerListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onPlayerLogin(PlayerLoginEvent event) {
        onLogin(event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Core.runTaskLater(() -> {
            CorePlayerJoinDelayedEvent e = new CorePlayerJoinDelayedEvent(Core.getPlayerManager().getPlayer(event.getPlayer()), event.getJoinMessage());
            Core.callEvent(e);
            event.setJoinMessage(e.getJoinMessage());
        }, 10L);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        onLeave(event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerKick(PlayerKickEvent event) {
        onLeave(event.getPlayer());
    }

    public void onLeave(Player player) {
        Core.getPlayerManager().getPlayer(player).resetManagers();
        Core.getPlayerManager().playerLoggedOut(player);
    }

    public void onLogin(Player player) {
        Core.getPlayerManager().playerLoggedIn(player);
    }
}

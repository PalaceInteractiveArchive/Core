package com.palacemc.core.player.impl;

import com.palacemc.core.Core;
import com.palacemc.core.events.CorePlayerJoinDelayedEvent;
import com.palacemc.core.player.CPlayer;
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
            CPlayer player =  Core.getPlayerManager().getPlayer(event.getPlayer());
            player.resetManagers();
            Core.callEvent(new CorePlayerJoinDelayedEvent(player));
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

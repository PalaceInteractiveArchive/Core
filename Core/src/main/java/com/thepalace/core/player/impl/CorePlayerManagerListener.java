package com.thepalace.core.player.impl;

import com.thepalace.core.Core;
import com.thepalace.core.events.CorePlayerJoinDelayedEvent;
import com.thepalace.core.player.CPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

import java.util.UUID;

public class CorePlayerManagerListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onPlayerLogin(AsyncPlayerPreLoginEvent event) {
        Core.getPlayerManager().playerLoggedIn(event.getUniqueId(), event.getName());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Core.getPlayerManager().playerJoined(event.getPlayer().getUniqueId());
        Core.runTaskLater(() -> {
            CorePlayerJoinDelayedEvent delayedEvent = new CorePlayerJoinDelayedEvent(Core.getPlayerManager().getPlayer(event.getPlayer()), event.getJoinMessage());
            Core.callEvent(delayedEvent);
            event.setJoinMessage(delayedEvent.getJoinMessage());
        }, 10L);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Core.getPlayerManager().playerLoggedOut(event.getPlayer().getUniqueId());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerKick(PlayerKickEvent event) {
        Core.getPlayerManager().playerLoggedOut(event.getPlayer().getUniqueId());
    }
}

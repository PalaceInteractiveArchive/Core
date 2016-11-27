package com.thepalace.core.player.impl;

import com.thepalace.core.Core;
import com.thepalace.core.events.CorePlayerJoinDelayedEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

import java.util.UUID;

public class CorePlayerManagerListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerLogin(AsyncPlayerPreLoginEvent event) {
        if (event.getLoginResult() == AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            Core.getPlayerManager().playerLoggedIn(event.getUniqueId());
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Call join delayed event
        Core.runTaskLater(() -> {
            Core.getPlayerManager().getPlayer(event.getPlayer()).setStatus(PlayerStatus.JOINED);
            CorePlayerJoinDelayedEvent e = new CorePlayerJoinDelayedEvent(Core.getPlayerManager().getPlayer(event.getPlayer()), event.getJoinMessage());
            Core.callEvent(e);
            event.setJoinMessage(e.getJoinMessage());
        }, 10L);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        onLeave(event.getPlayer().getUniqueId());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerKick(PlayerKickEvent event) {
        onLeave(event.getPlayer().getUniqueId());
    }

    public void onLeave(UUID uuid) {
        Core.getPlayerManager().getPlayer(uuid).resetManagers();
        Core.getPlayerManager().getPlayer(uuid).setStatus(PlayerStatus.LEFT);
        Core.getPlayerManager().playerLoggedOut(uuid);
    }
}

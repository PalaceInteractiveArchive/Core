package network.palace.core.player.impl.listeners;

import network.palace.core.Core;
import org.bukkit.ChatColor;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.*;

/**
 * The type Core player manager listener.
 */
public class CorePlayerManagerListener implements Listener {

    /**
     * On player login.
     *
     * @param event the event
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onPlayerLogin(AsyncPlayerPreLoginEvent event) {
        if (Core.isStarting()) {
            event.setKickMessage(ChatColor.AQUA + "Players can not join right now. Try again in a few seconds!");
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            return;
        }
        if (!Core.isDashboardAndSqlDisabled()) {
            if (Core.getDashboardConnection() == null || !Core.getDashboardConnection().isConnected() || Core.getSqlUtil() == null || Core.getSqlUtil().getConnection() == null) {
                event.setKickMessage(ChatColor.AQUA + "Players can not join right now. Try again in a few seconds!");
                event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
                return;
            }
        }
        if (event.getLoginResult().equals(AsyncPlayerPreLoginEvent.Result.ALLOWED)) {
            Core.getPlayerManager().playerLoggedIn(event.getUniqueId(), event.getName());
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerLoginMonitor(AsyncPlayerPreLoginEvent event) {
        if (!event.getLoginResult().equals(AsyncPlayerPreLoginEvent.Result.ALLOWED)) {
            Core.getPlayerManager().removePlayer(event.getUniqueId());
        }
    }

    /**
     * On player join.
     *
     * @param event the event
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage("");
        Player player = event.getPlayer();
        player.setExp(0);
        player.setLevel(0);
        Core.getPlayerManager().playerJoined(player);
    }

    /**
     * On player quit.
     *
     * @param event the event
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Core.getPlayerManager().playerLoggedOut(event.getPlayer());
        event.setQuitMessage("");
    }

    /**
     * On player kick.
     *
     * @param event the event
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerKick(PlayerKickEvent event) {
        Core.getPlayerManager().playerLoggedOut(event.getPlayer());
        event.setLeaveMessage("");
    }

    /**
     * On player achievement awarded.
     *
     * @param event the event
     */
    @EventHandler
    public void onPlayerAchievementAwarded(PlayerAdvancementDoneEvent event) {
        Player player = event.getPlayer();
        Advancement adv = event.getAdvancement();
        for (String criteria : adv.getCriteria()) {
            player.getAdvancementProgress(adv).revokeCriteria(criteria);
        }
    }

    /**
     * On player pickup item.
     *
     * @param event the event
     */
    @EventHandler
    public void onPlayerPickupItem(EntityPickupItemEvent event) {
        if (event.getItem().hasMetadata("special")) {
            if (event.getItem().getMetadata("special").get(0).asBoolean()) {
                event.setCancelled(true);
            }
        }
    }
}

package network.palace.core.player.impl.listeners;

import network.palace.core.Core;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
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
            if (Core.getDashboardConnection() == null || !Core.getDashboardConnection().isConnected() || Core.getMongoHandler() == null) {
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
        Core.getPermissionManager().logout(event.getPlayer().getUniqueId());
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
        Core.getPermissionManager().logout(event.getPlayer().getUniqueId());
    }

    /*@EventHandler
    public void onPlayerTabComplete(PlayerChatTabCompleteEvent event) {
        CPlayer player = Core.getPlayerManager().getPlayer(event.getPlayer());
        if (player == null) {
            event.getTabCompletions().clear();
            return;
        }
        Rank rank = player.getRank();
        Bukkit.broadcastMessage(event.getChatMessage() + " | " + event.getLastToken() + " | " + Arrays.asList(event.getTabCompletions()));
    }*/

    /**
     * On player achievement awarded.
     *
     * @param event the event
     */
    @EventHandler
    public void onPlayerAchievementAwarded(PlayerAchievementAwardedEvent event) {
        event.setCancelled(true);
    }

    /**
     * On player pickup item.
     *
     * @param event the event
     */
    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        if (event.getItem().hasMetadata("special")) {
            if (event.getItem().getMetadata("special").get(0).asBoolean()) {
                event.setCancelled(true);
            }
        }
    }
}

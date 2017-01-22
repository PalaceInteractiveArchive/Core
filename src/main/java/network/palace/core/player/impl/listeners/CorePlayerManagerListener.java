package network.palace.core.player.impl.listeners;

import com.comphenix.protocol.wrappers.WrappedGameProfile;
import network.palace.core.Core;
import network.palace.core.dashboard.packets.dashboard.PacketGetPack;
import network.palace.core.player.CPlayer;
import network.palace.core.player.impl.CorePlayerDefaultScoreboard;
import org.bukkit.Achievement;
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

    private CorePlayerDefaultScoreboard defaultScoreboard;

    /**
     * Instantiates a new Core player manager listener.
     */
    public CorePlayerManagerListener() {
        defaultScoreboard = new CorePlayerDefaultScoreboard();
    }

    /**
     * On player login.
     *
     * @param event the event
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onPlayerLogin(AsyncPlayerPreLoginEvent event) {
        if (Core.getDashboardConnection() == null || !Core.getDashboardConnection().isConnected() || Core.getSqlUtil() == null || Core.getSqlUtil().getConnection() == null || Core.isStarting()) {
            event.setKickMessage(ChatColor.AQUA + "Players can not join right now. Try again in a few seconds!");
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            return;
        }
        if (event.getLoginResult() == AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            Core.getPlayerManager().playerLoggedIn(event.getUniqueId(), event.getName());
        } else {
            Core.getPlayerManager().playerLoggedOut(event.getUniqueId());
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
        final Player player = event.getPlayer();
        if (!player.hasAchievement(Achievement.OPEN_INVENTORY)) {
            player.awardAchievement(Achievement.OPEN_INVENTORY);
        }
        WrappedGameProfile wrappedGameProfile = WrappedGameProfile.fromPlayer(player);
        String textureHash = wrappedGameProfile.getProperties().get("textures").iterator().next().getValue();
        Core.getPlayerManager().playerJoined(player.getUniqueId(), textureHash);
        Core.getDashboardConnection().send(new PacketGetPack(player.getUniqueId(), ""));
        Core.runTaskLater(() -> {
            CPlayer cPlayer = Core.getPlayerManager().getPlayer(player);
            cPlayer.getScoreboard().setupPlayerTags();
            for (CPlayer otherPlayer : Core.getPlayerManager().getOnlinePlayers()) {
                cPlayer.getScoreboard().addPlayerTag(otherPlayer);
                otherPlayer.getScoreboard().addPlayerTag(cPlayer);
            }
            defaultScoreboard.setup(cPlayer);
        }, 5);
    }

    /**
     * On player quit.
     *
     * @param event the event
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        onQuitOrKick(event.getPlayer());
        event.setQuitMessage("");
    }

    /**
     * On player kick.
     *
     * @param event the event
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerKick(PlayerKickEvent event) {
        onQuitOrKick(event.getPlayer());
        event.setLeaveMessage("");
    }

    private void onQuitOrKick(Player player) {
        CPlayer eventPlayer = Core.getPlayerManager().getPlayer(player);
        if (eventPlayer != null) {
            for (CPlayer otherPlayer : Core.getPlayerManager().getOnlinePlayers()) {
                eventPlayer.getScoreboard().removePlayerTag(otherPlayer);
                otherPlayer.getScoreboard().removePlayerTag(eventPlayer);
            }
        }
        Core.getPlayerManager().playerLoggedOut(player.getUniqueId());
    }

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

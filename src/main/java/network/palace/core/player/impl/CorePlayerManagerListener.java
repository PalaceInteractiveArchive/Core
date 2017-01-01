package network.palace.core.player.impl;

import com.comphenix.protocol.wrappers.WrappedGameProfile;
import network.palace.core.Core;
import network.palace.core.dashboard.packets.dashboard.PacketGetPack;
import network.palace.core.events.CorePlayerJoinDelayedEvent;
import network.palace.core.player.CPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class CorePlayerManagerListener implements Listener {

    private Core instance = Core.getPlugin(Core.class);
    private CorePlayerDefaultScoreboard defaultScoreboard;

    public CorePlayerManagerListener() {
        defaultScoreboard = new CorePlayerDefaultScoreboard();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onPlayerLogin(AsyncPlayerPreLoginEvent event) {
        if (!instance.getDashboardConnection().isConnected() && !instance.getDashboardConnection().isDisabled()) {
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

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onPlayerJoin(PlayerJoinEvent event) {
        String textureHash = "";
        final Player player = event.getPlayer();
        try {
            WrappedGameProfile wrappedGameProfile = WrappedGameProfile.fromPlayer(player);
            textureHash = wrappedGameProfile.getProperties().get("textures").iterator().next().getValue();
        } catch (Exception ignored) {
        }
        Core.getPlayerManager().playerJoined(player.getUniqueId(), textureHash);
        CorePlayerJoinDelayedEvent delayedEvent = new CorePlayerJoinDelayedEvent(Core.getPlayerManager().getPlayer(player));
        Core.runTaskLater(() -> {
            delayedEvent.call();
            PacketGetPack packet = new PacketGetPack(player.getUniqueId(), "");
            Core.getInstance().getDashboardConnection().send(packet);
        }, 10L);
        event.setJoinMessage("");
        event.getPlayer().setCollidable(false);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerJoinDelayed(CorePlayerJoinDelayedEvent event) {
        CPlayer player = event.getPlayer();
        // Set op
        boolean op = player.getRank().isOp();
        if (player.isOp() != op) {
            player.setOp(op);
        }
        // Scoreboard
        player.getScoreboard().setupPlayerTags();
        for (CPlayer otherPlayer : Core.getPlayerManager().getOnlinePlayers()) {
            player.getScoreboard().addPlayerTag(otherPlayer);
            otherPlayer.getScoreboard().addPlayerTag(player);
        }
        defaultScoreboard.setup(player);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        onQuitOrKick(event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerKick(PlayerKickEvent event) {
        onQuitOrKick(event.getPlayer());
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
}

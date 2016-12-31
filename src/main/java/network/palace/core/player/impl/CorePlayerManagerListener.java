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
        CorePlayerJoinDelayedEvent delayedEvent = new CorePlayerJoinDelayedEvent(Core.getPlayerManager()
                .getPlayer(player), event.getJoinMessage());

        Core.runTaskLater(() -> {
            delayedEvent.call();
            event.setJoinMessage(delayedEvent.getJoinMessage());
            PacketGetPack packet = new PacketGetPack(player.getUniqueId(), "");
            Core.getInstance().getDashboardConnection().send(packet);
        }, 10L);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        CPlayer player = Core.getPlayerManager().getPlayer(event.getPlayer());
        if (player != null) player.getScoreboard().destroy();
        Core.getPlayerManager().playerLoggedOut(event.getPlayer().getUniqueId());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerKick(PlayerKickEvent event) {
        CPlayer player = Core.getPlayerManager().getPlayer(event.getPlayer());
        if (player != null) player.getScoreboard().destroy();
        Core.getPlayerManager().playerLoggedOut(event.getPlayer().getUniqueId());
    }
}

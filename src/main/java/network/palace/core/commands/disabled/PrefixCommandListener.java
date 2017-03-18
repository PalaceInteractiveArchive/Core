package network.palace.core.commands.disabled;

import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class PrefixCommandListener implements Listener {

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (event.getMessage().startsWith("/minecraft:") || event.getMessage().startsWith("/bukkit:")) {
            CPlayer player = Core.getPlayerManager().getPlayer(event.getPlayer());
            if (player != null && player.getRank().getRankId() >= Rank.CHARACTER.getRankId()) return;
            event.getPlayer().sendMessage(ChatColor.RED + "Disabled");
            event.setCancelled(true);
        }
    }
}

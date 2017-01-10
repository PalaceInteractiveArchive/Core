package network.palace.core.commands.disabled;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class PrefixCommandListener implements Listener {

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (event.getMessage().startsWith("/minecraft:") || event.getMessage().startsWith("/bukkit:")) {
            event.getPlayer().sendMessage(ChatColor.RED + "Disabled");
            event.setCancelled(true);
        }
    }
}

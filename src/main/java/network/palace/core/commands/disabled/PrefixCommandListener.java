package network.palace.core.commands.disabled;

import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.TabCompleteEvent;

import java.util.ArrayList;
import java.util.List;

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

    @EventHandler
    public void onTabComplete(TabCompleteEvent event) {
        List<String> newCompletions =  new ArrayList<>();
        List<String> completions =  event.getCompletions();
        if (event.getSender() instanceof Player) {
            CPlayer player = Core.getPlayerManager().getPlayer((Player) event.getSender());
            if (player == null || player.getRank().getRankId() < Rank.SQUIRE.getRankId()) {
                for (String completion : completions) {
                    if (!completion.startsWith("/minecraft:") && !completion.startsWith("/bukkit:")
                            && !completion.startsWith("/ncp") && !completion.startsWith("/nocheatplus")
                            && !completion.startsWith("/core:")) {
                        newCompletions.add(completion);
                    }
                }
                event.setCompletions(newCompletions);
            }
        }
    }
}

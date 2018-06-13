package network.palace.core.commands.disabled;

import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.TabCompleteEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PrefixCommandListener implements Listener {
    private static List<String> blockedCompletions = new ArrayList<>(Arrays.asList("/minecraft:", "/bukkit:", "/worldedit",
            "/ncp", "/nocheatplus", "//"));

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (event.getMessage().startsWith("/minecraft:") || event.getMessage().startsWith("/bukkit:")) {
            CPlayer player = Core.getPlayerManager().getPlayer(event.getPlayer());
            if (player == null || player.getRank().getRankId() < Rank.TRAINEE.getRankId()) {
                event.getPlayer().sendMessage(ChatColor.RED + "Disabled");
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onTabComplete(TabCompleteEvent event) {
        List<String> newCompletions = new ArrayList<>();
        List<String> completions = event.getCompletions();
        if (event.getSender() instanceof Player) {
            CPlayer player = Core.getPlayerManager().getPlayer((Player) event.getSender());
            if (player == null || player.getRank().getRankId() < Rank.TRAINEE.getRankId()) {
                for (String completion : completions) {
                    boolean block = false;
                    for (String s : blockedCompletions) {
                        if (completion.startsWith(s)) {
                            block = true;
                            break;
                        }
                    }

                    if (!block && completion.contains(":")) {
                        int start = completion.startsWith("/") ? 1 : 0;
                        String prefix = completion.substring(start, completion.indexOf(":")).toLowerCase();
                        String suffix = completion.substring(completion.indexOf(":") + 1);
                        if (!prefix.contains(" ")) {
                            for (Plugin p : Bukkit.getPluginManager().getPlugins()) {
                                if (p.getName().equalsIgnoreCase(prefix)) {
                                    blockedCompletions.add((start == 1 ? "/" : "") + prefix);
                                    block = true;
                                    break;
                                }
                            }
                        }
                    }

                    if (block) continue;

                    newCompletions.add(completion);
                }
                newCompletions.sort(String.CASE_INSENSITIVE_ORDER);
                event.setCompletions(newCompletions);
            }
        }
    }
}

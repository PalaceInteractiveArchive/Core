package network.palace.core.player.impl;

import network.palace.core.Core;
import network.palace.core.events.EconomyUpdateEvent;
import network.palace.core.player.CPlayer;
import network.palace.core.player.CPlayerScoreboardManager;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CorePlayerDefaultScoreboard implements Listener {

    public CorePlayerDefaultScoreboard() {
        if (!isDefaultSidebarEnabled()) return;
        Core.registerListener(this);
        Core.runTaskTimer(() -> {
            for (CPlayer player : Core.getPlayerManager().getOnlinePlayers()) {
                if (player.getStatus() != CPlayer.PlayerStatus.JOINED) return;
                if (!player.getScoreboard().isSetup()) return;
                setOnlinePlayers(player.getScoreboard());
            }
        }, 0L, 10L);
    }

    public void setup(CPlayer player) {
        if (!isDefaultSidebarEnabled()) return;
        CPlayerScoreboardManager scoreboard = player.getScoreboard();
        // Title
        scoreboard.title(ChatColor.GOLD + "" + ChatColor.BOLD + "The " + ChatColor.DARK_PURPLE + ChatColor.BOLD + "Palace " + ChatColor.GOLD + "" + ChatColor.BOLD + "Network");
        // Blank space
        scoreboard.setBlank(8);
        // Balance
        setBalance(7, scoreboard, Core.getEconomy().getBalance(player.getUuid()));
        // Blank space
        scoreboard.setBlank(6);
        // Tokens
        setTokens(5, scoreboard, Core.getEconomy().getTokens(player.getUuid()));
        // Blank space
        scoreboard.setBlank(4);
        // Rank
        scoreboard.set(3, ChatColor.GREEN + "Rank: " + player.getRank().getTagColor() + player.getRank().getName());
        // Blank space
        scoreboard.setBlank(2);
        // Players number
        setOnlinePlayers(scoreboard);
        // Server name
        scoreboard.set(0, ChatColor.GREEN + "Server: " + Core.getInstance().getServerType());
    }

    @EventHandler
    public void onEconomyUpdate(EconomyUpdateEvent event) {
        int amount = event.getAmount();
        boolean isBalance = event.isBalance();
        CPlayer player = Core.getPlayerManager().getPlayer(event.getUuid());
        if (player == null) return;
        if (isBalance) {
            setBalance(7, player.getScoreboard(), amount);
        } else {
            setTokens(5, player.getScoreboard(), amount);
        }
    }

    public void setOnlinePlayers(CPlayerScoreboardManager scoreboard) {
        scoreboard.set(1, ChatColor.GREEN + "Online Players: " + Core.getPlayerManager().getOnlinePlayers().size());
    }

    private void setTokens(int position, CPlayerScoreboardManager scoreboard, int tokens) {
        if (tokens > 2147483646) {
            scoreboard.set(position, ChatColor.GREEN + "\u272a " + 2147483646 + "+");
        } else {
            scoreboard.set(position, ChatColor.GREEN + "\u272a " + tokens);
        }
    }

    private void setBalance(int position, CPlayerScoreboardManager scoreboard, int balance) {
        if (balance > 2147483646) {
            scoreboard.set(position, ChatColor.GREEN + "$ " + 2147483646 + "+");
        } else {
            scoreboard.set(position, ChatColor.GREEN + "$ " + balance);
        }
    }

    private boolean isDefaultSidebarEnabled() {
        return Core.getCoreConfig().getBoolean("isDefaultSidebarEnabled", true);
    }
}

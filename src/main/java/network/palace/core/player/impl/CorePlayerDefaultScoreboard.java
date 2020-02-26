package network.palace.core.player.impl;

import network.palace.core.Core;
import network.palace.core.events.CoreOnlineCountUpdate;
import network.palace.core.events.EconomyUpdateEvent;
import network.palace.core.player.CPlayer;
import network.palace.core.player.CPlayerScoreboardManager;
import network.palace.core.player.PlayerStatus;
import network.palace.core.player.RankTag;
import network.palace.core.utils.MiscUtil;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * The type Core player default scoreboard.
 */
public class CorePlayerDefaultScoreboard implements Listener {
    private int playerCount = 0;

    /**
     * Instantiates a new Core player default scoreboard.
     */
    public CorePlayerDefaultScoreboard() {
        if (!isDefaultSidebarEnabled()) return;
        Core.registerListener(this);
    }

    /**
     * Setups the scoreboard for the player.
     *
     * @param player the player
     */
    public void setup(CPlayer player) {
        if (!isDefaultSidebarEnabled()) return;
        CPlayerScoreboardManager scoreboard = player.getScoreboard();
        for (int i = 15; i > 11; i--) {
            scoreboard.remove(i);
        }
        // Title
        scoreboard.title(ChatColor.GOLD + "" + ChatColor.BOLD + "The " + ChatColor.DARK_PURPLE + ChatColor.BOLD + "Palace " + ChatColor.GOLD + "" + ChatColor.BOLD + "Network");
        List<RankTag> tags = player.getTags();
        int tagOffset = tags.size() > 0 ? tags.size() + 1 : 0;
        // Blank space
        scoreboard.setBlank(10 + tagOffset);
        // Balance temp
        scoreboard.set(9 + tagOffset, ChatColor.GREEN + "$ Loading...");
        // Blank space
        scoreboard.setBlank(8 + tagOffset);
        // Tokens temp
        scoreboard.set(7 + tagOffset, ChatColor.GREEN + "\u272a Loading...");
        // Blank space
        scoreboard.setBlank(6 + tagOffset);
        // Rank
        scoreboard.set(5 + tagOffset, ChatColor.GREEN + "Rank: " + player.getRank().getTagColor() + player.getRank().getName());
        if (tags.size() > 0) {
            scoreboard.setBlank(4 + tagOffset);
            for (int i = tags.size(); i > 0; i--) {
                RankTag tag = tags.get(tags.size() - i);
                scoreboard.set(4 + i, tag.getColor() + "" + ChatColor.ITALIC + "" + tag.getName());
            }
        }
        // Blank space
        scoreboard.setBlank(4);
        // Players number
        scoreboard.set(3, ChatColor.GREEN + "Online Players: " + playerCount);
        // Server name
        scoreboard.set(2, ChatColor.GREEN + "Server: " + Core.getServerType());
        // Blank
        scoreboard.setBlank(1);
        // Store link #Sellout
        scoreboard.set(0, ChatColor.YELLOW + "store.palace.network");
        // Load balance async
        loadBalance(player, scoreboard, 9 + tagOffset);
        // Load tokens async
        loadTokens(player, scoreboard, 7 + tagOffset);
    }

    /**
     * On online count update.
     *
     * @param event the event
     */
    @EventHandler
    public void onOnlineCountUpdate(CoreOnlineCountUpdate event) {
        playerCount = event.getCount();
        for (CPlayer player : Core.getPlayerManager().getOnlinePlayers()) {
            if (player.getStatus() != PlayerStatus.JOINED || !player.getScoreboard().isSetup()) continue;
            player.getScoreboard().set(3, ChatColor.GREEN + "Online Players: " + MiscUtil.formatNumber(playerCount));
        }
    }

    /**
     * On economy update.
     *
     * @param event the event
     */
    @EventHandler
    public void onEconomyUpdate(EconomyUpdateEvent event) {
        int amount = event.getAmount();
        CPlayer player = Core.getPlayerManager().getPlayer(event.getUuid());
        if (player == null) return;
        List<RankTag> tags = player.getTags();
        int tagOffset = tags.size() > 0 ? tags.size() + 1 : 0;
        switch (event.getCurrency()) {
            case BALANCE:
                setBalance(9 + tagOffset, player.getScoreboard(), amount);
                break;
            case TOKENS:
                setTokens(7 + tagOffset, player.getScoreboard(), amount);
                break;
        }
    }

    public void loadTokens(CPlayer player, CPlayerScoreboardManager scoreboard, int position) {
        Core.runTaskAsynchronously(Core.getInstance(), () -> {
            int tokens = player.getTokens();
            Core.callSyncMethod(Core.getInstance(), (Callable<Object>) () -> {
                setTokens(position, scoreboard, tokens);
                return true;
            });
        });
    }

    public void loadBalance(CPlayer player, CPlayerScoreboardManager scoreboard, int position) {
        Core.runTaskAsynchronously(Core.getInstance(), () -> {
            int balance = player.getBalance();
            Core.callSyncMethod(Core.getInstance(), (Callable<Object>) () -> {
                setBalance(position, scoreboard, balance);
                return true;
            });
        });
    }

    private void setTokens(int position, CPlayerScoreboardManager scoreboard, int tokens) {
        if (tokens > (Integer.MAX_VALUE - 1)) {
            scoreboard.set(position, ChatColor.GREEN + "\u272a " + (Integer.MAX_VALUE - 1) + "+");
        } else {
            scoreboard.set(position, ChatColor.GREEN + "\u272a " + tokens);
        }
    }

    private void setBalance(int position, CPlayerScoreboardManager scoreboard, int balance) {
        if (balance > (Integer.MAX_VALUE - 1)) {
            scoreboard.set(position, ChatColor.GREEN + "$ " + (Integer.MAX_VALUE - 1) + "+");
        } else {
            scoreboard.set(position, ChatColor.GREEN + "$ " + balance);
        }
    }

    private boolean isDefaultSidebarEnabled() {
        return Core.getCoreConfig().getBoolean("isDefaultSidebarEnabled", true);
    }
}

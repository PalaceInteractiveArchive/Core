package network.palace.core.player.impl.managers;

import lombok.Getter;
import network.palace.core.player.CPlayer;
import network.palace.core.player.CPlayerScoreboardManager;
import network.palace.core.player.Rank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;

/**
 * The type Core player scoreboard manager.
 */
public class CorePlayerScoreboardManager implements CPlayerScoreboardManager {

    private static final int MAX_TITLE_LENGTH = 32;
    private static final int MAX_STRING_LENGTH = 64;

    private final CPlayer player;

    private HashMap<Integer, String> lines;
    private Scoreboard scoreboard;
    private Objective scoreboardObjective;
    private String title;
    private int nullIndex = 0;
    @Getter private boolean isSetup = false;

    /**
     * Instantiates a new Core player scoreboard manager.
     *
     * @param player the player
     */
    public CorePlayerScoreboardManager(CPlayer player) {
        this.player = player;
    }

    /**
     * Set a sidebar scoreboard value
     * @param id   the id of the location in the sidebar
     * @param text the text to show
     * @return scoreboard manager
     */
    @Override
    public CPlayerScoreboardManager set(int id, String text) {
        text = text.substring(0, Math.min(text.length(), MAX_STRING_LENGTH));
        while (text.endsWith("\u00A7")) text = text.substring(0, text.length() - 1);
        if (lines.containsKey(id)) {
            if (lines.get(id).equals(text) || (ChatColor.stripColor(lines.get(id)).trim().equals("") && ChatColor.stripColor(text).trim().equals(""))) {
                return this;
            } else {
                remove(id);
            }
        }
        lines.values().removeIf(text::equals);
        lines.put(id, text);
        if (scoreboardObjective == null) return this;
        if (scoreboardObjective.getScore(text) == null) return this;
        scoreboardObjective.getScore(text).setScore(id);
        return this;
    }

    /**
     * Create a blank entry at an id
     * @param id the id to create as blank
     * @return scoreboard manager
     */
    @Override
    public CPlayerScoreboardManager setBlank(int id) {
        return set(id, nextNull());
    }

    /**
     * Remove an id from the sidebar
     * @param id the id to remove
     * @return scoreboard manager
     */
    @Override
    public CPlayerScoreboardManager remove(int id) {
        if (lines.containsKey(id)) {
            scoreboard.resetScores(lines.get(id));
        }
        lines.remove(id);
        return this;
    }

    /**
     * Set the title of a sidebar scoreboard
     * @param title the title to use
     * @return scoreboard manager
     */
    @Override
    public CPlayerScoreboardManager title(String title) {
        if (title == null) return this;
        if (scoreboard == null) setup();

        this.title = title.substring(0, Math.min(title.length(), MAX_TITLE_LENGTH));
        if (scoreboardObjective == null) setup();
        try {
            if (scoreboardObjective != null) scoreboardObjective.setDisplayName(title);
        } catch (IllegalStateException | IllegalArgumentException ignored) {
        }
        if (scoreboard != null && player != null && player.getBukkitPlayer() != null) {
            player.getBukkitPlayer().setScoreboard(scoreboard);
        }
        return this;
    }

    /**
     * Setup the sidebar for a player
     */
    private void setup() {
        lines = new HashMap<>();
        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        if (scoreboard == null) return;
        if (player.getBukkitPlayer() == null) return;
        player.getBukkitPlayer().setScoreboard(scoreboard);
        scoreboardObjective = scoreboard.registerNewObjective(player.getName(), "dummy");
        scoreboardObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
        isSetup = true;
    }

    private String nextNull() {
        String s;
        do {
            nullIndex = (nullIndex + 1) % ChatColor.values().length;
            s = ChatColor.values()[nullIndex].toString();
        } while (lines.containsValue(s));
        return s;
    }

    /**
     * Setup the player tags to prevent collision.
     */
    @Override
    public void setupPlayerTags() {
        if (scoreboard == null) setup();
        for (Rank rank : Rank.values()) {
            Team team = scoreboard.registerNewTeam(rank.getName());
            team.setPrefix(rank.getScoreboardName());
            team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
        }
    }

    /**
     * Add a tag to a player
     * @param otherPlayer the other player
     */
    @Override
    public void addPlayerTag(CPlayer otherPlayer) {
        if (scoreboard == null) setup();
        if (otherPlayer == null || otherPlayer.getRank() == null) return;
        if (scoreboard.getTeam(otherPlayer.getRank().getName()) == null) return;
        if (scoreboard.getTeam(otherPlayer.getRank().getName()).hasEntry(otherPlayer.getName())) return;
        scoreboard.getTeam(otherPlayer.getRank().getName()).addEntry(otherPlayer.getName());
    }

    /**
     * Remove a tag from a player
     * @param otherPlayer the other player
     */
    @Override
    public void removePlayerTag(CPlayer otherPlayer) {
        if (scoreboard == null) setup();
        if (otherPlayer == null || otherPlayer.getRank() == null) return;
        if (scoreboard.getTeam(otherPlayer.getRank().getName()) == null) return;
        if (!scoreboard.getTeam(otherPlayer.getRank().getName()).hasEntry(otherPlayer.getName())) return;
        scoreboard.getTeam(otherPlayer.getRank().getName()).removeEntry(otherPlayer.getName());
    }

    /**
     * Clear the player's scoreboard
     */
    @Override
    public void clear() {
        setup();
    }
}

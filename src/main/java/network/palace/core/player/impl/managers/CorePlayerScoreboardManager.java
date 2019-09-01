package network.palace.core.player.impl.managers;

import lombok.Getter;
import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.core.player.CPlayerScoreboardManager;
import network.palace.core.player.Rank;
import network.palace.core.player.SponsorTier;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * The type Core player scoreboard manager.
 */
public class CorePlayerScoreboardManager implements CPlayerScoreboardManager {

    private static final int MAX_STRING_LENGTH = 64;

    private final CPlayer player;

    private Map<Integer, String> lines = new HashMap<>();
    private Scoreboard scoreboard;
    private Objective scoreboardObjective;
    private String title = "";
    @Getter private boolean isSetup = false;
    private boolean tagsVisible = true;

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
     *
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
     *
     * @param id the id to create as blank
     * @return scoreboard manager
     */
    @Override
    public CPlayerScoreboardManager setBlank(int id) {
        return set(id, getBlanks(id));
    }

    /**
     * Remove an id from the sidebar
     *
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
     *
     * @param title the title to use
     * @return scoreboard manager
     */
    @Override
    public CPlayerScoreboardManager title(String title) {
        if (this.title != null && this.title.equals(title)) return this;
        if (scoreboard == null) setup();
        if (title == null) return this;

        this.title = title;
        if (scoreboardObjective == null) setup();
        try {
            if (scoreboardObjective != null) scoreboardObjective.setDisplayName(title);
        } catch (IllegalStateException | IllegalArgumentException ignored) {
        }
        if (scoreboard != null && player != null && player.getBukkitPlayer() != null)
            player.getBukkitPlayer().setScoreboard(scoreboard);
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

    private String getBlanks(int id) {
        String[] blank = new String[id + 1];
        Arrays.fill(blank, ChatColor.WHITE.toString());
        return String.join("", blank);
    }

    /**
     * Setup the player tags to prevent collision.
     */
    @Override
    public void setupPlayerTags() {
        if (scoreboard == null) setup();
        for (Rank rank : Rank.values()) {
            if (scoreboard.getTeam(rank.getScoreboardPrefix() + rank.getName()) != null) continue;
            Team team = scoreboard.registerNewTeam(rank.getScoreboardPrefix() + rank.getName());
            team.setPrefix(rank.getFormattedName() + " ");
            team.setColor(rank.getTagColor());
            team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
            team.setSuffix("");
        }
    }

    /**
     * Add a tag to a player
     *
     * @param otherPlayer the other player
     */
    @Override
    public void addPlayerTag(CPlayer otherPlayer) {
        if (scoreboard == null) setup();
        if (otherPlayer == null || otherPlayer.getRank() == null || otherPlayer.getSponsorTier() == null) return;
        String teamName = teamName(otherPlayer.getRank(), otherPlayer.getSponsorTier());
        Team team = scoreboard.getTeam(teamName);

        if (team == null) {
            team = scoreboard.registerNewTeam(teamName);
            team.setPrefix(otherPlayer.getRank().getFormattedName() + " ");
            team.setSuffix(otherPlayer.getSponsorTier().getScoreboardTag());
            team.setColor(otherPlayer.getRank().getTagColor());
            team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
            team.addEntry(otherPlayer.getName());
        } else if (!team.hasEntry(otherPlayer.getName())) {
            team.addEntry(otherPlayer.getName());
        }
    }

    /**
     * Remove a tag from a player
     *
     * @param otherPlayer the other player
     */
    @Override
    public void removePlayerTag(CPlayer otherPlayer) {
        if (scoreboard == null) setup();
        if (otherPlayer == null || otherPlayer.getRank() == null) return;
        String teamName = teamName(otherPlayer.getRank(), otherPlayer.getSponsorTier());
        Team team = scoreboard.getTeam(teamName);

        if (team == null) return;
        team.removeEntry(otherPlayer.getName());
    }

    private String teamName(Rank rank, SponsorTier tier) {
        return rank.getScoreboardPrefix() + (rank.equals(Rank.SPECIALGUEST) ? "sg" : rank.getDBName()) + (tier.equals(SponsorTier.NONE) ? "" : tier.getDBName().substring(0, 4));
    }

    /**
     * Clear the player's scoreboard
     */
    @Override
    public void clear() {
        Core.runTask(Core.getInstance(), this::setup);
    }

    @Override
    public void toggleTags() {
        tagsVisible = !tagsVisible;
        if (scoreboard == null || scoreboard.getTeams() == null) return;
        if (tagsVisible) {
            for (Team team : scoreboard.getTeams()) {
                team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);
            }
        } else {
            for (Team team : scoreboard.getTeams()) {
                team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
            }
        }
    }

    @Override
    public void toggleTags(boolean hidden) {
        if (tagsVisible == !hidden) return;
        toggleTags();
    }

    @Override
    public boolean getTagsVisible() {
        return tagsVisible;
    }
}

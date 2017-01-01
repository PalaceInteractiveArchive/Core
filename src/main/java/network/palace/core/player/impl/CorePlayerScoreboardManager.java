package network.palace.core.player.impl;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import network.palace.core.Core;
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
import java.util.concurrent.ThreadLocalRandom;

public class CorePlayerScoreboardManager implements CPlayerScoreboardManager {

    private static final int MAX_STRING_LENGTH = 64;

    private final CPlayer player;

    private BiMap<Integer, String> lines;
    private Scoreboard scoreboard;
    private Objective scoreboardObjective;
    private String title;
    private int nullIndex = 0;

    public CorePlayerScoreboardManager(CPlayer player) {
        this.player = player;
    }

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
        if (lines.containsValue(text)) {
            lines.inverse().remove(text);
        }
        lines.put(id, text);
        if (scoreboardObjective == null) return this;
        if (scoreboardObjective.getScore(text) == null) return this;
        scoreboardObjective.getScore(text).setScore(id);
        return this;
    }

    @Override
    public CPlayerScoreboardManager setBlank(int id) {
        return set(id, nextNull());
    }

    @Override
    public CPlayerScoreboardManager remove(int id) {
        if (lines.containsKey(id)) {
            scoreboard.resetScores(lines.get(id));
        }
        lines.remove(id);
        return this;
    }

    @Override
    public CPlayerScoreboardManager title(String title) {
        if (this.title != null && this.title.equals(title)) return this;

        this.title = title;
        scoreboardObjective.setDisplayName(title);
        player.getBukkitPlayer().setScoreboard(scoreboard);
        return this;
    }

    private void setup() {
        lines = HashBiMap.create();
        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

        if (scoreboard == null) return;
        if (player.getBukkitPlayer() == null) return;

        player.getBukkitPlayer().setScoreboard(scoreboard);

        scoreboardObjective = scoreboard.registerNewObjective("obj" + ThreadLocalRandom.current().nextInt(1000000), "dummy");
        scoreboardObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    private String nextNull() {
        String s;
        do {
            nullIndex = (nullIndex + 1) % ChatColor.values().length;
            s = ChatColor.values()[nullIndex].toString();
        } while (lines.containsValue(s));
        return s;
    }

    @Override
    public void setupPlayerTags() {
        if (scoreboard == null) {
            setup();
            for (Rank rank : Rank.values()) {
                Team team = scoreboard.registerNewTeam(rank.getName());
                team.setPrefix(rank.getScoreboardName());
            }
        }
    }

    @Override
    public void addPlayerTag(CPlayer otherPlayer) {
        if (scoreboard == null) setup();
        if (scoreboard.getTeam(otherPlayer.getRank().getName()) != null) removePlayerTag(otherPlayer);
        scoreboard.getTeam(otherPlayer.getRank().getName()).addEntry(otherPlayer.getName());
    }

    @Override
    public void removePlayerTag(CPlayer otherPlayer) {
        if (scoreboard == null) setup();
        if (scoreboard.getTeam(otherPlayer.getRank().getName()) == null) return;
        scoreboard.getTeam(otherPlayer.getRank().getName()).removeEntry(otherPlayer.getName());
    }
}

package network.palace.core.player.impl;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import network.palace.core.player.CPlayer;
import network.palace.core.player.CPlayerScoreboardManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.concurrent.ThreadLocalRandom;

public class CorePlayerScoreboardManager implements CPlayerScoreboardManager {

    private final static String OBJECTIVE = "obj" + ThreadLocalRandom.current().nextInt(1000000000);
    private final static int MAX_STRING_LENGTH = 64;

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
        if (scoreboard == null) setup();
        text = text.substring(0, Math.min(text.length(), MAX_STRING_LENGTH));
        while (text.endsWith("§")) text = text.substring(0, text.length() - 1);
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
        if (scoreboard == null) setup();
        if (lines.containsKey(id)) {
            scoreboard.resetScores(lines.get(id));
        }
        lines.remove(id);
        return this;
    }

    @Override
    public CPlayerScoreboardManager title(String title) {
        if (scoreboard == null) setup();
        if (this.title != null && this.title.equals(title)) return this;
        if (scoreboardObjective == null) setup();

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

        scoreboardObjective = scoreboard.registerNewObjective(OBJECTIVE, "dummy");
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
}

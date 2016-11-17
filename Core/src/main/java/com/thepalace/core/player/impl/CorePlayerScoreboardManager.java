package com.thepalace.core.player.impl;

import com.thepalace.core.player.CPlayer;
import com.thepalace.core.player.CPlayerScoreboardManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CorePlayerScoreboardManager implements CPlayerScoreboardManager {

    private CPlayer player;
    private List<TeamFillerEntry> items;
    private String objectiveName = "Default";

    public CorePlayerScoreboardManager(CPlayer player) {
        this.player = player;
        this.items = new ArrayList<>(0);
    }

    @Override
    public String getTitle() {
        return player.getBukkitPlayer().getScoreboard().getObjective(objectiveName).getDisplayName();
    }

    @Override
    public void setTitle(String title) {
        Objective objective = player.getBukkitPlayer().getScoreboard().getObjective(objectiveName);
        if (objective == null) {
            String subTitle = title.length() <= 16 ? title : title.substring(0, 15);
            objectiveName = subTitle;
            objective = player.getBukkitPlayer().getScoreboard().registerNewObjective(subTitle, subTitle);
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        }
        objective.setDisplayName(title);
        fillOutScoreboard();
    }

    @Override
    public String getLine(int line) {
        return items.get(line).getString();
    }

    @Override
    public void setLine(int line, String info) {
        if (info == null || info.equals("")) {
            info = getNextEmptyFiller();
        }
        if (line == items.size()) {  // If the item we are adding is the next item, without need for any more fillers
            items.add(tryToMatchWithTeam(info));
            fillOutScoreboard();
        } else if (line > items.size()) { // More than 1 item needs added, lets start filling out
            for (int pointer = items.size(); pointer <= line; pointer++) {
                setLine(pointer, null);  // Fill out scoreboard to this point
            }
            setLine(line, info);// Will now work, since we filled it out
        } else {
            TeamFillerEntry tfe = items.get(line);
            if (tfe != null) {
                if (tfe.getTeam() != null) {
                    tfe.getTeam().removeEntry(tfe.getInner());
                    if (tfe.getTeam().getEntries().isEmpty()) {
                        tfe.getTeam().unregister();
                    }
                }
                player.getBukkitPlayer().getScoreboard().resetScores(tfe.getInner()); // Removes the current stupid version
            }
            items.set(line, tryToMatchWithTeam(info)); // Set item in
            fillOutScoreboard(); // Reset scores to fit
        }
    }

    @Override
    public void setToSize(int i) {
        if (i == items.size()) return; // No action needed
        if (i < items.size()) {  // Need to get smaller
            items = items.subList(0, i - 1);
        } else {
            while (items.size() <= i) {
                setLine(items.size(), "");// Add a new line to the bottom
            }
        }
    }

    @Override
    public void setScore(String line, int score) {
        Objective objective = player.getBukkitPlayer().getScoreboard().getObjective(objectiveName);
        objective.getScore(line).setScore(score);
    }

    @Override
    public void reset() {
        player.getBukkitPlayer().setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
    }

    private void fillOutScoreboard() {
        int index = items.size();
        Objective objective = player.getBukkitPlayer().getScoreboard().getObjective(objectiveName);
        if (objective == null) {
            objective = player.getBukkitPlayer().getScoreboard().registerNewObjective(objectiveName, objectiveName);
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        }
        for (TeamFillerEntry item : items) {
            objective.getScore(item.getInner()).setScore(index);
            index--;
        }
    }

    private String getNextEmptyFiller() {
        List<Integer> index = new ArrayList<>(); // Initial filler array. Will be used to fill in a random color code sequence
        index.add(0); // Initial &0 value
        while (true) { // While we haven't found a unique string
            StringBuilder builder = new StringBuilder();
            for (int currIndex : index) { // For each of our entries
                char[] charArray = new char[currIndex];
                Arrays.fill(charArray, ' ');
                String empty = new String(charArray);
                builder.append(empty);
            }
            String current = builder.toString();
            if (innerExists(current)) { // If our items contain this filler
                // Well fuck gotta try the next one
                // Increment the last. If the last is 16, lapse to 0 and go down the line until we run out of stuff
                int pointer = index.size() - 1;
                while (true) {
                    if (index.get(pointer) == 15) { // EOL
                        index.set(pointer, 0);
                        if (pointer != 0) {
                            pointer--;
                        } else {
                            // Out of room, must append
                            index.add(0); // Just add a 0 to the end.
                            break;
                        }
                    } else {
                        index.set(pointer, index.get(pointer) + 1); // Increment the last one
                        break;
                    }
                }
            } else {
                return current;
            }
        }
    }

    private boolean innerExists(String s) {
        for (TeamFillerEntry e : items) {
            if (e.getInner().equals(s)) {
                return true;
            }
        }
        return false;
    }

    // This will go through our current teams that are defined for this scoreboard, and try to match a prefix and suffix with it if needed.
    private TeamFillerEntry tryToMatchWithTeam(String string) {
        if (string.length() > 80) {
            throw new IllegalArgumentException("Input string is too long!");
        }
        if (string.length() <= 16) {
            return new TeamFillerEntry(string, null); // Don't need a wrapper team.
        }
        // Otherwise, we do need a team. Lets go through our current teams and see if we can maybe find a match
        for (Team t : player.getBukkitPlayer().getScoreboard().getTeams()) {
            if (string.startsWith(t.getPrefix()) && string.endsWith(t.getSuffix())) {
                // Now that our basic check is done, lets make sure we are not putting too many characters in the middle
                // prefix chars + 16 + suffix chars  Must be more than the input string
                if (t.getPrefix().length() + t.getSuffix().length() + 16 >= string.length()) {
                    // And lets also make sure that we are not leaving the center empty, for that is illegal.
                    if (t.getPrefix().length() + t.getSuffix().length() < string.length()) {
                        // Alright. We must also make sure our derived string is unique, and is not currently an online player.
                        String inner = string.substring(t.getPrefix().length() - 1, string.length() - t.getSuffix().length() - 1);
                        if (Bukkit.getPlayer(inner) != null) {
                            // We can now declare this inner as part of this team, and return the final TeamFillerEntry
                            t.addEntry(inner);
                            return new TeamFillerEntry(inner, t);
                        }
                    }
                }
            }
        }
        // If we did not find a suitable team, lets just create our own.
        int w = ((string.length() - 17) / 2) + 1; // Indexing plus 16 inner value
        String prefix = string.substring(0, w);
        String inner = string.substring(w, w + 16);
        String end = string.substring(w + 16);
        Team t = player.getBukkitPlayer().getScoreboard().registerNewTeam(inner);
        t.setPrefix(prefix);
        t.setSuffix(end);
        t.addEntry(inner);
        return new TeamFillerEntry(inner, t);
    }

    private static class TeamFillerEntry {

        @Getter private String inner;
        @Getter private Team team;

        public TeamFillerEntry(String inner, Team team) {
            this.inner = inner;
            this.team = team;
        }

        public String getString() {
            if (team == null) return inner;
            return team.getPrefix() + inner + team.getSuffix();
        }
    }
}

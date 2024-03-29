package network.palace.core.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;

import java.util.List;

@AllArgsConstructor
public enum RankTag {
    /* Dev Team (best team) */
    JRDEV("Jr. Developer Program", "J", "j", ChatColor.DARK_RED, 9),
    /* Media Team */
    DESIGNER("Resource Pack Designer", "D", "d", ChatColor.BLUE, 8),
    /* Guide Program */
    GUIDE("Guide Team", "G", "g", ChatColor.DARK_GREEN, 7),
    /* Sponsor Tiers */
    SPONSOR_OBSIDIAN("Obsidian Tier Sponsor", "S", "s_o", ChatColor.DARK_PURPLE, 6),
    SPONSOR_EMERALD("Emerald Tier Sponsor", "S", "s_e", ChatColor.GREEN, 5),
    SPONSOR_DIAMOND("Diamond Tier Sponsor", "S", "s_d", ChatColor.AQUA, 4),
    SPONSOR_LAPIS("Lapis Tier Sponsor", "S", "s_l", ChatColor.BLUE, 3),
    SPONSOR_GOLD("Gold Tier Sponsor", "S", "s_g", ChatColor.YELLOW, 2),
    SPONSOR_IRON("Iron Tier Sponsor", "S", "s_i", ChatColor.GRAY, 1),
    /* Creator Tag */
    CREATOR("Creator", "C", "c", ChatColor.BLUE, 0),
    /* No Tags */
    NONE("", "", "", ChatColor.RESET, -1);

    @Getter private String name;
    private String tag;
    @Getter private String shortScoreboardTag;
    @Getter private ChatColor color;
    @Getter private int id;

    public String getShortTag() {
        return tag;
    }

    public String getTag() {
        return ChatColor.WHITE + "[" + color + ChatColor.BOLD + tag + ChatColor.WHITE + "] ";
    }

    /**
     * Get tag object from a string
     *
     * @param name tag name in string
     * @return tag object
     */
    public static RankTag fromString(String name) {
        if (name == null || name.isEmpty()) return null;

        for (RankTag tag : RankTag.values()) {
            if (!tag.equals(NONE) && tag.getDBName().equalsIgnoreCase(name)) return tag;
        }
        return null;
    }

    public String getDBName() {
        return name().toLowerCase();
    }

    public String getScoreboardTag() {
        return " " + ChatColor.WHITE + "[" + color + tag + ChatColor.WHITE + "]";
    }

    public static String formatChat(List<RankTag> tags) {
        tags.sort((rankTag, t1) -> t1.id - rankTag.id);
        StringBuilder s = new StringBuilder();
        for (RankTag tag : tags) {
            s.append(tag.getTag());
        }
        return s.toString();
    }

    public static String formatScoreboardSuffix(List<RankTag> tags) {
        if (tags.isEmpty()) return "";
        tags.sort((rankTag, t1) -> t1.id - rankTag.id);
        RankTag tag = tags.get(0);
        return ChatColor.WHITE + " [" + tag.getColor() + ChatColor.BOLD + tag.getShortTag() + ChatColor.WHITE + "]";
    }

    public static String formatScoreboardSidebar(List<RankTag> tags) {
        tags.sort((rankTag, t1) -> t1.id - rankTag.id);
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < tags.size(); i++) {
            RankTag tag = tags.get(i);
            s.append(tag.getColor()).append(tag.getShortTag());
            if (i <= (tags.size() - 1)) {
                s.append(" ");
            }
        }
        return s.toString();
    }
}

package network.palace.core.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;

@AllArgsConstructor
public enum SponsorTier {
    NONE("", null),
    IRON("Iron", ChatColor.GRAY),
    GOLD("Gold", ChatColor.YELLOW),
    LAPIS("Lapis", ChatColor.BLUE),
    DIAMOND("Diamond", ChatColor.AQUA),
    EMERALD("Emerald", ChatColor.GREEN);

    @Getter private String name;
    @Getter private ChatColor color;

    /**
     * Get rank object from a string
     *
     * @param name rank name in string
     * @return rank object
     */
    public static SponsorTier fromString(String name) {
        if (name == null || name.isEmpty()) return NONE;

        for (SponsorTier tier : SponsorTier.values()) {
            if (tier.getDBName().equalsIgnoreCase(name)) return tier;
        }
        return NONE;
    }

    public String getDBName() {
        return name.toLowerCase();
    }

    public String getScoreboardTag() {
        if (this.equals(NONE)) return "";
        return " " + ChatColor.WHITE + "[" + color + "S" + ChatColor.WHITE + "]";
    }

    /**
     * Get the tier's tag for chat
     *
     * @param space whether or not to include a space after the tag
     * @return the word "Sponsor" colored to the tier in white brackets
     */
    public String getChatTag(boolean space) {
        if (this.equals(NONE)) return "";
        return ChatColor.WHITE + "[" + color + "Sponsor" + ChatColor.WHITE + "]" + (space ? " " : "");
    }
}
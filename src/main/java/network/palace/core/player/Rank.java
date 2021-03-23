package network.palace.core.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import network.palace.core.Core;
import org.bukkit.ChatColor;

import java.util.Map;

@AllArgsConstructor
public enum Rank {
    OWNER("Owner", ChatColor.RED + "Owner ", ChatColor.RED, ChatColor.YELLOW, true, 13),
    EXEC("Executive", ChatColor.RED + "Director ", ChatColor.RED, ChatColor.YELLOW, true, 13),
    MANAGER("Manager", ChatColor.GOLD + "Manager ", ChatColor.GOLD, ChatColor.YELLOW, true, 13),
    LEAD("Lead", ChatColor.GREEN + "Lead ", ChatColor.DARK_GREEN, ChatColor.GREEN, true, 13),
    DEVELOPER("Developer", ChatColor.BLUE + "Developer ", ChatColor.BLUE, ChatColor.AQUA, true, 13),
    COORDINATOR("Coordinator", ChatColor.BLUE + "Coordinator ", ChatColor.BLUE, ChatColor.AQUA, true, 12),
    BUILDER("Imagineer", ChatColor.AQUA + "Imagineer ", ChatColor.AQUA, ChatColor.AQUA, true, 11),
    IMAGINEER("Imagineer", ChatColor.AQUA + "Imagineer ", ChatColor.AQUA, ChatColor.AQUA, true, 11),
    MEDIA("Cast Member", ChatColor.BLUE + "CM ", ChatColor.AQUA, ChatColor.AQUA, true, 11),
    CM("Cast Member", ChatColor.AQUA + "CM ", ChatColor.AQUA, ChatColor.AQUA, true, 11),
    TRAINEETECH("Trainee", ChatColor.AQUA + "Trainee ", ChatColor.AQUA, ChatColor.AQUA, false, 10),
    TRAINEEBUILD("Trainee", ChatColor.AQUA + "Trainee ", ChatColor.AQUA, ChatColor.AQUA, false, 10),
    TRAINEE("Trainee", ChatColor.DARK_GREEN + "Trainee ", ChatColor.DARK_GREEN, ChatColor.DARK_GREEN, false, 9),
    CHARACTER("Character", ChatColor.DARK_PURPLE + "Character ", ChatColor.DARK_PURPLE, ChatColor.DARK_PURPLE, false, 8),
    INFLUENCER("Influencer", ChatColor.DARK_PURPLE + "Influencer ", ChatColor.DARK_PURPLE, ChatColor.WHITE, false, 7),
    VIP("VIP", ChatColor.DARK_PURPLE + "VIP ", ChatColor.DARK_PURPLE, ChatColor.WHITE, false, 7),
    SHAREHOLDER("Shareholder", ChatColor.LIGHT_PURPLE + "Shareholder ", ChatColor.LIGHT_PURPLE, ChatColor.WHITE, false, 6),
    CLUB("Club 33", ChatColor.DARK_RED + "C33 ", ChatColor.DARK_RED, ChatColor.WHITE, false, 5),
    DVC("DVC", ChatColor.GOLD + "DVC ", ChatColor.GOLD, ChatColor.WHITE, false, 4),
    PASSPORT("Premier Passport", ChatColor.YELLOW + "Premier ", ChatColor.YELLOW, ChatColor.WHITE, false, 3),
    PASSHOLDER("Passholder", ChatColor.DARK_AQUA + "Passholder ", ChatColor.DARK_AQUA, ChatColor.WHITE, false, 2),
    GUEST("Guest", ChatColor.GRAY + "", ChatColor.GRAY, ChatColor.GRAY, false, 1);

    private static final char[] alphabet = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};

    @Getter private String name;
    @Getter private String scoreboardName;
    @Getter private ChatColor tagColor;
    @Getter private ChatColor chatColor;
    @Getter private boolean isOp;
    @Getter private int rankId;

    public static Rank fromString(String name) {
        if (name == null) return GUEST;
        if (name.equalsIgnoreCase("admin")) return LEAD;
        String rankName = name.replaceAll(" ", "");

        for (Rank rank : Rank.values()) {
            if (rank.getDBName().equalsIgnoreCase(rankName)) return rank;
        }
        return GUEST;
    }

    @Deprecated
    public String getSqlName() {
        return getDBName();
    }

    public String getDBName() {
        String s;
        switch (this) {
            case TRAINEEBUILD:
            case TRAINEETECH:
                s = name().toLowerCase();
                break;
            default:
                s = name.toLowerCase().replaceAll(" ", "");
        }
        return s;
    }

    @Deprecated
    public String getNameWithBrackets() {
        return getFormattedName();
    }

    /**
     * Get the formatted name of a rank
     *
     * @return the rank name with any additional formatting that should exist
     */
    public String getFormattedName() {
        if (getName() == "Premier Passport") {
            return getTagColor() + "Premier";
        }
        return getTagColor() + getName();
    }

    public String getFormattedScoreboardName() {
        return getTagColor() + getScoreboardName();
    }

    /**
     * Get the permissions that belong to the rank
     *
     * @return the permissions, and the status of the permission
     */
    public Map<String, Boolean> getPermissions() {
        return Core.getPermissionManager().getPermissions(this);
    }

    public String getScoreboardName() {
        int pos = ordinal();
        if (pos < 0 || pos >= alphabet.length) return "";
        if (getName() == "Premier Passport") {
            return String.valueOf(alphabet[pos] + "Premier");
        }
        return String.valueOf(alphabet[pos] + getName());

    }

    public String getScoreboardPrefix() {
        int pos = ordinal();
        if (pos < 0 || pos >= alphabet.length) return "";
        return String.valueOf(alphabet[pos]);
    }

    public String getShortName() {
        return getName().substring(0, 3);
    }
}

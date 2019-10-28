package network.palace.core.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import network.palace.core.Core;
import org.bukkit.ChatColor;

import java.util.Map;

@AllArgsConstructor
public enum Rank {
    DIRECTOR("Director", "a", ChatColor.RED + "Director ", ChatColor.RED, ChatColor.YELLOW, true, 13),
    MANAGER("Manager", "b", ChatColor.RED + "Manager ", ChatColor.RED, ChatColor.YELLOW, true, 13),
    ADMIN("Admin", "c", ChatColor.RED + "Admin ", ChatColor.RED, ChatColor.YELLOW, true, 13),
    DEVELOPER("Developer", "d", ChatColor.GOLD + "Developer ", ChatColor.GOLD, ChatColor.YELLOW, true, 13),
    COORDINATOR("Coordinator", "e", ChatColor.YELLOW + "Coordinator ", ChatColor.YELLOW, ChatColor.GREEN, true, 12),
    ARCHITECT("Architect", "f", ChatColor.YELLOW + "Architect ", ChatColor.YELLOW, ChatColor.GREEN, true, 12),
    BUILDER("Builder", "g", ChatColor.GREEN + "Builder ", ChatColor.GREEN, ChatColor.GREEN, true, 11),
    MOD("Mod", "h", ChatColor.GREEN + "Mod ", ChatColor.GREEN, ChatColor.GREEN, true, 11),
    TRAINEEBUILD("Trainee", "i", ChatColor.DARK_GREEN + "Trainee ", ChatColor.DARK_GREEN, ChatColor.DARK_GREEN, false, 10),
    TRAINEE("Trainee", "i", ChatColor.DARK_GREEN + "Trainee ", ChatColor.DARK_GREEN, ChatColor.DARK_GREEN, false, 9),
    CHARACTER("Character", "j", ChatColor.BLUE + "Character ", ChatColor.BLUE, ChatColor.BLUE, false, 8),
    SPECIALGUEST("Special Guest", "k", ChatColor.DARK_PURPLE + "SG ", ChatColor.DARK_PURPLE, ChatColor.WHITE, false, 7),
    SHAREHOLDER("Shareholder", "l", ChatColor.LIGHT_PURPLE + "Shareholder ", ChatColor.LIGHT_PURPLE, ChatColor.WHITE, false, 6),
    HONORABLE("Honorable", "m", ChatColor.LIGHT_PURPLE + "Honorable ", ChatColor.LIGHT_PURPLE, ChatColor.WHITE, false, 5),
    MAJESTIC("Majestic", "n", ChatColor.DARK_PURPLE + "Majestic ", ChatColor.DARK_PURPLE, ChatColor.WHITE, false, 4),
    NOBLE("Noble", "o", ChatColor.BLUE + "Noble ", ChatColor.BLUE, ChatColor.WHITE, false, 3),
    DWELLER("Dweller", "p", ChatColor.AQUA + "Dweller ", ChatColor.AQUA, ChatColor.WHITE, false, 2),
    SETTLER("Settler", "q", ChatColor.GRAY + "", ChatColor.DARK_AQUA, ChatColor.WHITE, false, 1);

    @Getter private String name;
    @Getter private String scoreboardPrefix;
    @Getter private String scoreboardName;
    @Getter private ChatColor tagColor;
    @Getter private ChatColor chatColor;
    @Getter private boolean isOp;
    @Getter private int rankId;

    public static Rank fromString(String name) {
        if (name == null) return SETTLER;
        String rankName = name.replaceAll(" ", "");

        for (Rank rank : Rank.values()) {
            if (rank.getDBName().equalsIgnoreCase(rankName)) return rank;
        }
        return SETTLER;
    }

    @Deprecated
    public String getSqlName() {
        return getDBName();
    }

    public String getDBName() {
        if (this.equals(TRAINEEBUILD)) {
            return "traineebuild";
        }
        return name.toLowerCase().replaceAll(" ", "");
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
        String bold = getRankId() >= Rank.TRAINEE.getRankId() ? "" + ChatColor.BOLD : "";
        return getTagColor() + bold + getName();
    }

    /**
     * Get the permissions that belong to the rank
     *
     * @return the permissions, and the status of the permission
     */
    public Map<String, Boolean> getPermissions() {
        return Core.getPermissionManager().getPermissions(this);
    }
}

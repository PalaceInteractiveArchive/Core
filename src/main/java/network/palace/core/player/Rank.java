package network.palace.core.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import network.palace.core.Core;
import org.bukkit.ChatColor;

import java.util.HashMap;

@AllArgsConstructor
public enum Rank {

    EMPRESS("Empress", ChatColor.RED + "Empress ", ChatColor.RED, ChatColor.YELLOW, true, 11),
    EMPEROR("Emperor", ChatColor.RED + "Emperor ", ChatColor.RED, ChatColor.YELLOW, true, 11),
    WIZARD("Wizard", ChatColor.GOLD + "Wizard ", ChatColor.GOLD, ChatColor.YELLOW, true, 11),
    PALADIN("Paladin", ChatColor.YELLOW + "Paladin ", ChatColor.YELLOW, ChatColor.GREEN, true, 10),
    ARCHITECT("Architect", ChatColor.GREEN + "Architect ", ChatColor.GREEN, ChatColor.GREEN, true, 9),
    KNIGHT("Knight", ChatColor.GREEN + "Knight ", ChatColor.GREEN, ChatColor.GREEN, true, 9),
    SQUIRE("Squire", ChatColor.DARK_GREEN + "Squire ", ChatColor.DARK_GREEN, ChatColor.DARK_GREEN, false, 8),
    CHARACTER("Character", ChatColor.BLUE + "Character ", ChatColor.BLUE, ChatColor.BLUE, false, 7),
    SPECIALGUEST("Special Guest", ChatColor.DARK_PURPLE + "SG ", ChatColor.DARK_PURPLE, ChatColor.WHITE, false, 6),
    MCPROHOSTING("MCProHosting", ChatColor.RED + "MCPro ", ChatColor.RED, ChatColor.WHITE, false, 6),
    HONORABLE("Honorable", ChatColor.LIGHT_PURPLE + "Honorable ", ChatColor.LIGHT_PURPLE, ChatColor.WHITE, false, 5),
    MAJESTIC("Majestic", ChatColor.DARK_PURPLE + "Majestic ", ChatColor.DARK_PURPLE, ChatColor.WHITE, false, 4),
    NOBLE("Noble", ChatColor.BLUE + "Noble " ,ChatColor.BLUE, ChatColor.WHITE, false, 3),
    SHAREHOLDER("Shareholder", ChatColor.LIGHT_PURPLE + "Shareholder ", ChatColor.LIGHT_PURPLE, ChatColor.WHITE, false, 3),
    DWELLER("Dweller", ChatColor.AQUA + "Dweller ", ChatColor.AQUA, ChatColor.WHITE, false, 2),
    DVCMEMBER("DVC", ChatColor.AQUA + "DVC ", ChatColor.AQUA, ChatColor.WHITE, false, 2),
    SETTLER("Settler", ChatColor.GRAY + "", ChatColor.DARK_AQUA, ChatColor.WHITE, false, 1);

    @Getter private String name;
    @Getter private String scoreboardName;
    @Getter private ChatColor tagColor;
    @Getter private ChatColor chatColor;
    @Getter private boolean isOp;
    @Getter private int rankId;

    public static Rank fromString(String string) {
        String rankName = string.toLowerCase();
        switch (rankName) {
            case "empress":
                return EMPRESS;
            case "emperor":
                return EMPEROR;
            case "wizard":
                return WIZARD;
            case "paladin":
                return PALADIN;
            case "architect":
                return ARCHITECT;
            case "knight":
                return KNIGHT;
            case "squire":
                return SQUIRE;
            case "character":
                return CHARACTER;
            case "specialguest":
                return SPECIALGUEST;
            case "mcprohosting":
                return MCPROHOSTING;
            case "honorable":
                return HONORABLE;
            case "majestic":
                return MAJESTIC;
            case "noble":
                return NOBLE;
            case "dweller":
                return DWELLER;
            case "shareholder":
                return SHAREHOLDER;
            case "dvc":
                return DVCMEMBER;
            default:
                return SETTLER;
        }
    }

    public String getSqlName() {
        return name.toLowerCase().replaceAll(" ", "");
    }

    public String getNameWithBrackets() {
        return ChatColor.WHITE + "[" + getTagColor() + getName() + ChatColor.WHITE + "]";
    }

    /**
     * Get the formatted name of a rank
     *
     * @return the rank name with any additional formatting that should exist
     */
    public String getFormattedName() {
        String bold = getRankId() >= 8 ? "" + ChatColor.BOLD : "";
        return bold + getTagColor() + getName();
    }

    /**
     * Get the permissions that belong to the rank
     *
     * @return the permissions, and the status of the permission
     */
    public HashMap<String, Boolean> getPermissions() {
        return Core.getPermissionManager().getPermissions(this);
    }
}

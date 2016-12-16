package network.palace.core.player;

import org.bukkit.ChatColor;

@SuppressWarnings({"CanBeFinal", "unused"})
public enum Rank {
    EMPRESS("Empress", ChatColor.RED, ChatColor.YELLOW, true, 11),
    EMPEROR("Emperor", ChatColor.RED, ChatColor.YELLOW, true, 11),
    WIZARD("Wizard", ChatColor.GOLD, ChatColor.YELLOW, true, 11),
    PALADIN("Paladin", ChatColor.YELLOW, ChatColor.GREEN, true, 10),
    ARCHITECT("Architect", ChatColor.GREEN, ChatColor.GREEN, true, 9),
    KNIGHT("Knight", ChatColor.GREEN, ChatColor.GREEN, true, 9),
    SQUIRE("Squire", ChatColor.DARK_GREEN, ChatColor.DARK_GREEN, false, 8),
    CHARACTER("Character", ChatColor.BLUE, ChatColor.BLUE, false, 7),
    SPECIALGUEST("Special Guest", ChatColor.DARK_PURPLE, ChatColor.WHITE, false, 6),
    MCPROHOSTING("MCProHosting", ChatColor.RED, ChatColor.WHITE, false, 6),
    HONORABLE("Honorable", ChatColor.LIGHT_PURPLE, ChatColor.WHITE, false, 5),
    MAJESTIC("Majestic", ChatColor.DARK_PURPLE, ChatColor.WHITE, false, 4),
    NOBLE("Noble", ChatColor.BLUE, ChatColor.WHITE, false, 3),
    DWELLER("Dweller", ChatColor.AQUA, ChatColor.WHITE, false, 2),
    SHAREHOLDER("Shareholder", ChatColor.LIGHT_PURPLE, ChatColor.WHITE, false, 3),
    DVCMEMBER("DVC", ChatColor.AQUA, ChatColor.WHITE, false, 2),
    SETTLER("Settler", ChatColor.DARK_AQUA, ChatColor.WHITE, false, 1);

    public String name;
    public ChatColor tagColor;
    public ChatColor chatColor;
    public boolean op;
    public int rankId;

    Rank(String name, ChatColor tagColor, ChatColor chatColor, boolean op, int rankId) {
        this.name = name;
        this.tagColor = tagColor;
        this.chatColor = chatColor;
        this.op = op;
        this.rankId = rankId;
    }

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

    public int getRankId() {
        return rankId;
    }

    public String getName() {
        return name;
    }

    public String getSqlName() {
        return name.toLowerCase().replaceAll(" ", "");
    }

    public String getNameWithBrackets() {
        return ChatColor.WHITE + "[" + getTagColor() + getName() + ChatColor.WHITE + "]";
    }

    public boolean getOp() {
        return op;
    }

    /*public HashMap<String, Boolean> getPermissions() {
        switch (this) {
            case EMPRESS:
            case EMPEROR:
                return MCMagicCore.permManager.emperorPerms;
            case WIZARD:
                return MCMagicCore.permManager.wizPerms;
            case PALADIN:
                return MCMagicCore.permManager.palPerms;
            case KNIGHT:
                return MCMagicCore.permManager.knightPerms;
            case SQUIRE:
                return MCMagicCore.permManager.squirePerms;
            case CHARACTER:
                return MCMagicCore.permManager.charPerms;
            case SPECIALGUEST:
            case MCPROHOSTING:
                return MCMagicCore.permManager.sgPerms;
            case HONORABLE:
            case MAJESTIC:
            case NOBLE:
            case DWELLER:
            case SHAREHOLDER:
            case DVCMEMBER:
                return MCMagicCore.permManager.dwellerPerms;
            default:
                return MCMagicCore.permManager.settlerPerms;
        }
    }*/

    public ChatColor getChatColor() {
        return chatColor;
    }

    public ChatColor getTagColor() {
        return tagColor;
    }
}

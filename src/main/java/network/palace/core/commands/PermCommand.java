package network.palace.core.commands;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CommandPermission;
import network.palace.core.command.CoreCommand;
import network.palace.core.dashboard.packets.dashboard.PacketRankChange;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

/**
 * The type Perm command.
 */
@CommandMeta(description = "Permissions command")
@CommandPermission(rank = Rank.PALADIN)
public class PermCommand extends CoreCommand {

    /**
     * Instantiates a new Perm command.
     */
    public PermCommand() {
        super("perm");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        Core.runTaskAsynchronously(new Runnable() {
            @Override
            public void run() {
                if (args.length == 1) {
                    String arg1 = args[0];
                    if (arg1.equalsIgnoreCase("refresh")) {
                        Core.getPermissionManager().refresh(sender);
                        return;
                    }
                    if (arg1.equalsIgnoreCase("list")) {
                        sender.sendMessage(ChatColor.YELLOW + "Valid Usage: " + ChatColor.GREEN + "/perm list groups");
                        return;
                    }
                    helpMenu(sender, arg1.equalsIgnoreCase("player") ? "player" : arg1.equalsIgnoreCase("group") ? "group" : arg1.equalsIgnoreCase("list") ? "list" : "main");
                    return;
                }
                if (args.length == 2) {
                    String arg1 = args[0];
                    String arg2 = args[1];
                    if (arg1.equalsIgnoreCase("list")) {
                        if (arg2.equalsIgnoreCase("groups")) {
                            sender.sendMessage(ChatColor.LIGHT_PURPLE + "Palace Ranks:");
                            for (Rank rank : Rank.values()) {
                                sender.sendMessage(ChatColor.GREEN + "- " + rank.getTagColor() + rank.getName());
                            }
                            return;
                        }
                        helpMenu(sender, "list");
                        return;
                    }
                }
                if (args.length == 3) {
                    String arg1 = args[0];
                    String arg2 = args[1];
                    String arg3 = args[2];
                    if (arg1.equalsIgnoreCase("player")) {
                        if (arg3.equalsIgnoreCase("setgroup")) {
                            helpMenu(sender, "player");
                            return;
                        }
                        if (!Core.getSqlUtil().playerExists(arg2)) {
                            sender.sendMessage(ChatColor.RED + "Player '" + arg2 + "' not found!");
                            return;
                        }
                        if (arg3.equalsIgnoreCase("getgroup")) {
                            Rank rank = Core.getSqlUtil().getRank(Core.getSqlUtil().getUniqueIdFromName(arg2));
                            sender.sendMessage(ChatColor.BLUE + arg2 + ChatColor.YELLOW + " is in the " + rank.getFormattedName() + ChatColor.YELLOW + " group.");
                            return;
                        }
                        helpMenu(sender, "player");
                        return;
                    }
                    if (arg1.equalsIgnoreCase("group")) {
                        Rank rank = Rank.fromString(arg2);
                        if (arg3.equalsIgnoreCase("members")) {
                            Core.getSqlUtil().getMembers(rank);
                            return;
                        }
                        if (arg3.equalsIgnoreCase("perms")) {
                            sender.sendMessage(ChatColor.GREEN + "Permissions of the " + rank.getFormattedName() + ChatColor.GREEN + "Rank:");
                            for (Map.Entry<String, Boolean> perm : rank.getPermissions().entrySet()) {
                                if (perm.getValue()) {
                                    sender.sendMessage(ChatColor.DARK_GREEN + "- " + ChatColor.YELLOW + perm.getKey() + " " + ChatColor.GREEN + "True");
                                } else {
                                    sender.sendMessage(ChatColor.DARK_GREEN + "- " + ChatColor.YELLOW + perm.getKey() + " " + ChatColor.RED + "False");
                                }
                            }
                            return;
                        }
                    }
                    helpMenu(sender, arg1.equalsIgnoreCase("group") ? "group" : arg1.equalsIgnoreCase("list") ? "list" : "main");
                    return;
                }
                if (args.length == 4) {
                    String type = args[0];
                    String playerName = args[1];
                    String action = args[2];
                    String rankName = args[3];
                    if (type.equalsIgnoreCase("player")) {
                        if (!Core.getSqlUtil().playerExists(playerName)) {
                            sender.sendMessage(ChatColor.RED + "Player not found!");
                            return;
                        }
                        switch (action) {
                            case "setgroup":
                                Rank rank = Rank.fromString(rankName);
                                Player tp = Bukkit.getPlayer(playerName);
                                UUID uuid;
                                if (tp != null) {
                                    uuid = tp.getUniqueId();
                                    Core.getPlayerManager().getPlayer(tp).setRank(rank);
                                } else {
                                    uuid = Core.getSqlUtil().getUniqueIdFromName(playerName);
                                }
                                Core.getSqlUtil().setRank(uuid, rank);
                                String source = sender instanceof Player ? sender.getName() : "Console on " + Core.getInstanceName();
                                PacketRankChange packet = new PacketRankChange(uuid, rank, source);
                                Core.getDashboardConnection().send(packet);
                                sender.sendMessage(ChatColor.YELLOW + playerName + "'s rank has been changed to " + rank.getFormattedName());
                                return;
                            case "get":
                                final Rank currentRank2 = Core.getSqlUtil().getRank(Core.getSqlUtil().getUniqueIdFromName(playerName));
                                Map<String, Boolean> permissions2 = currentRank2.getPermissions();
                                if (!permissions2.containsKey(rankName)) {
                                    sender.sendMessage(currentRank2.getFormattedName() + ChatColor.YELLOW + " does not set " + ChatColor.RED + rankName);
                                    return;
                                }
                                if (permissions2.get(rankName)) {
                                    sender.sendMessage(currentRank2.getFormattedName() + ChatColor.YELLOW + " sets " + ChatColor.YELLOW + rankName + ChatColor.YELLOW + " to " + ChatColor.GREEN + "true");
                                } else {
                                    sender.sendMessage(currentRank2.getFormattedName() + ChatColor.YELLOW + " sets " + ChatColor.YELLOW + rankName + ChatColor.YELLOW + " to " + ChatColor.RED + "false");
                                }
                                return;
                            default:
                                helpMenu(sender, "player");
                                return;
                        }
                    }
                    if (type.equalsIgnoreCase("group")) {
                        Rank rank = Rank.fromString(playerName);
                        Map<String, Boolean> permissions = rank.getPermissions();
                        switch (action) {
                            case "get":
                                if (!permissions.containsKey(rankName)) {
                                    sender.sendMessage(rank.getFormattedName() + ChatColor.YELLOW + " does not set " + ChatColor.RED + rankName);
                                    return;
                                }
                                if (permissions.get(rankName)) {
                                    sender.sendMessage(rank.getFormattedName() + ChatColor.YELLOW + " sets " + ChatColor.YELLOW + rankName + ChatColor.YELLOW + " to " + ChatColor.GREEN + ChatColor.BOLD + "true");
                                } else {
                                    sender.sendMessage(rank.getFormattedName() + ChatColor.YELLOW + " sets " + ChatColor.YELLOW + rankName + ChatColor.YELLOW + " to " + ChatColor.RED + ChatColor.BOLD + "false");
                                }
                                return;
                            case "set":
                                Core.getSqlUtil().setPermission(rankName, rank, true);
                                for (CPlayer tp : Core.getPlayerManager().getOnlinePlayers()) {
                                    if (tp.getRank().equals(rank)) {
                                        Core.getPermissionManager().attachments.get(tp.getUniqueId()).setPermission(rankName, true);
                                    }
                                }
                                sender.sendMessage(rank.getFormattedName() + ChatColor.YELLOW + " now sets " + ChatColor.AQUA + rankName + ChatColor.YELLOW + " to " + ChatColor.GREEN + "" + ChatColor.BOLD + "true");
                                return;
                            case "unset":
                                Core.getSqlUtil().unsetPermission(rankName, rank);
                                for (CPlayer tp : Core.getPlayerManager().getOnlinePlayers()) {
                                    if (tp.getRank().equals(rank)) {
                                        Core.getPermissionManager().attachments.get(tp.getUniqueId()).unsetPermission(rankName);
                                    }
                                }
                                sender.sendMessage(rank.getFormattedName() + ChatColor.YELLOW + " does not set " + ChatColor.AQUA + rankName + ChatColor.YELLOW + " anymore");
                                return;
                            default:
                                helpMenu(sender, "group");
                                return;
                        }
                    }
                }
                if (args.length == 5) {
                    String arg1 = args[0];
                    String arg2 = args[1];
                    String arg3 = args[2];
                    String arg4 = args[3];
                    String arg5 = args[4];
                    if (arg1.equalsIgnoreCase("group")) {
                        Rank rank = Rank.fromString(arg2);
                        if (arg3.equalsIgnoreCase("set")) {
                            boolean value = arg5.equalsIgnoreCase("true");
                            Core.getSqlUtil().setPermission(arg4, rank, value);
                            for (CPlayer tp : Core.getPlayerManager().getOnlinePlayers()) {
                                if (tp.getRank().equals(rank)) {
                                    Core.getPermissionManager().attachments.get(tp.getUniqueId()).setPermission(arg4, true);
                                }
                            }
                            if (value) {
                                sender.sendMessage(rank.getFormattedName() + ChatColor.YELLOW + " now sets " + ChatColor.AQUA + arg4 + ChatColor.YELLOW + " to " + ChatColor.GREEN + "" + ChatColor.BOLD + "true");
                                return;
                            }
                            sender.sendMessage(rank.getFormattedName() + ChatColor.YELLOW + " now sets " + ChatColor.AQUA + arg4 + ChatColor.YELLOW + " to " + ChatColor.RED + "" + ChatColor.BOLD + "false");
                            return;
                        }
                        helpMenu(sender, "group");
                        return;
                    }
                }
                helpMenu(sender, "main");
            }
        });
    }

    private void helpMenu(CommandSender sender, String menu) {
        sender.sendMessage(ChatColor.YELLOW + "----------------------------------------------------");
        switch (menu) {
            case "player":
                sender.sendMessage(ChatColor.YELLOW + "/perm player " + ChatColor.LIGHT_PURPLE + "<player> " + ChatColor.YELLOW + "get " + ChatColor.LIGHT_PURPLE + "<permission> " + ChatColor.YELLOW + "- Get the value of a permission for a player");
                sender.sendMessage(ChatColor.YELLOW + "/perm player " + ChatColor.LIGHT_PURPLE + "<player> " + ChatColor.YELLOW + "getgroup " + ChatColor.YELLOW + "- Get the player's rank.");
                sender.sendMessage(ChatColor.YELLOW + "/perm player " + ChatColor.LIGHT_PURPLE + "<player> " + ChatColor.YELLOW + "setgroup " + ChatColor.LIGHT_PURPLE + "<group> " + ChatColor.YELLOW + "- Set a player's rank");
                return;
            case "group":
                sender.sendMessage(ChatColor.YELLOW + "/perm group " + ChatColor.LIGHT_PURPLE + "<group> " + ChatColor.YELLOW + "get " + ChatColor.LIGHT_PURPLE + "<permission> " + ChatColor.YELLOW + "- Get the value of a permission for a rank");
                sender.sendMessage(ChatColor.YELLOW + "/perm group " + ChatColor.LIGHT_PURPLE + "<group> " + ChatColor.YELLOW + "set " + ChatColor.LIGHT_PURPLE + "<permission> " + ChatColor.AQUA + "[true/false] " + ChatColor.YELLOW + "- Set the value of a permission for a rank");
                sender.sendMessage(ChatColor.YELLOW + "/perm group " + ChatColor.LIGHT_PURPLE + "<group> " + ChatColor.YELLOW + "unset " + ChatColor.LIGHT_PURPLE + "<permission> " + ChatColor.YELLOW + "- Remove the value of a permission for a rank");
                sender.sendMessage(ChatColor.YELLOW + "/perm group " + ChatColor.LIGHT_PURPLE + "<group> " + ChatColor.YELLOW + "members " + ChatColor.YELLOW + "- List the members of a rank");
                return;
            case "list":
                sender.sendMessage(ChatColor.YELLOW + "/perm list " + ChatColor.LIGHT_PURPLE + "<what> " + ChatColor.GREEN + "- List groups");
                return;
            default:
                sender.sendMessage(ChatColor.YELLOW + "/perm player " + ChatColor.LIGHT_PURPLE + "<player> " + ChatColor.GREEN + "- Player commands");
                sender.sendMessage(ChatColor.YELLOW + "/perm group " + ChatColor.LIGHT_PURPLE + "<group> " + ChatColor.GREEN + "- Group commands");
                sender.sendMessage(ChatColor.YELLOW + "/perm list " + ChatColor.LIGHT_PURPLE + "<what> " + ChatColor.GREEN + "- List groups");
                sender.sendMessage(ChatColor.YELLOW + "/perm refresh " + ChatColor.GREEN + "- Refresh permissions for all groups");
        }
    }
}

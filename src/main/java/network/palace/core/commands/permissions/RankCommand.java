package network.palace.core.commands.permissions;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@CommandMeta(description = "Rank commands")
public class RankCommand extends CoreCommand {

    public RankCommand() {
        super("rank");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        handle(player.getBukkitPlayer(), args);
    }

    @Override
    protected void handleCommand(ConsoleCommandSender commandSender, String[] args) throws CommandException {
        handle(commandSender, args);
    }

    protected void handle(CommandSender sender, String[] args) throws CommandException {
        if (args.length < 2) {
            helpMenu(sender);
            return;
        }
        Rank rank = Rank.fromString(args[0]);
        switch (args[1].toLowerCase()) {
            case "get": {
                if (args.length < 3) {
                    helpMenu(sender);
                    return;
                }
                String node = args[2];
                Map<String, Boolean> perms = Core.getPermissionManager().getPermissions(rank);
                if (!perms.containsKey(node)) {
                    sender.sendMessage(ChatColor.YELLOW + "Rank " + rank.getFormattedName() + ChatColor.YELLOW + " doesn't set a value for " + ChatColor.AQUA + node);
                    return;
                }
                boolean val = perms.getOrDefault(node, false);
                if (val) {
                    sender.sendMessage(ChatColor.GREEN + "Rank " + rank.getFormattedName() + ChatColor.GREEN + " sets true for " + ChatColor.AQUA + node);
                } else {
                    sender.sendMessage(ChatColor.RED + "Rank " + rank.getFormattedName() + ChatColor.RED + " sets false for " + ChatColor.AQUA + node);
                }
                return;
            }
            case "set": {
                if (args.length < 3) {
                    helpMenu(sender);
                    return;
                }
                String node = args[2];
                Map<String, Boolean> perms = Core.getPermissionManager().getPermissions(rank);
                boolean val = true;
                if (args.length > 3) {
                    val = Boolean.parseBoolean(args[3]);
                }
                perms.put(node, val);
                boolean finalVal = val;
                Core.runTaskAsynchronously(Core.getInstance(), () -> {
                    Core.getMongoHandler().setPermission(node, rank, finalVal);
                    Core.getPermissionManager().setPermission(rank, node, finalVal);
                    Core.getPermissionManager().refresh();
                    if (finalVal) {
                        sender.sendMessage(ChatColor.GREEN + "Rank " + rank.getFormattedName() + ChatColor.GREEN + " now sets true for " + ChatColor.AQUA + node);
                    } else {
                        sender.sendMessage(ChatColor.RED + "Rank " + rank.getFormattedName() + ChatColor.RED + " now sets false for " + ChatColor.AQUA + node);
                    }
                });
                return;
            }
            case "unset": {
                if (args.length < 3) {
                    helpMenu(sender);
                    return;
                }
                String node = args[2];
                Map<String, Boolean> perms = Core.getPermissionManager().getPermissions(rank);
                if (!perms.containsKey(node)) {
                    sender.sendMessage(ChatColor.YELLOW + "Rank " + rank.getFormattedName() + ChatColor.YELLOW + " doesn't set " + ChatColor.AQUA + node);
                } else {
                    perms.remove(node);
                    Core.runTaskAsynchronously(Core.getInstance(), () -> {
                        Core.getPermissionManager().unsetPermission(rank, node);
                        Core.getMongoHandler().unsetPermission(node, rank);
                        Core.getPermissionManager().refresh();
                        sender.sendMessage(ChatColor.YELLOW + "Rank " + rank.getFormattedName() + ChatColor.YELLOW + " no longer sets " + ChatColor.AQUA + node);
                    });
                }
                return;
            }
            case "members": {
                List<Rank> blocked = Arrays.asList(Rank.SETTLER, Rank.DWELLER);
                List<String> members = null;
                if (!blocked.contains(rank)) {
                    members = Core.getMongoHandler().getMembers(rank);
                }
                if (members == null) {
                    sender.sendMessage(ChatColor.RED + "Too many members to list!");
                } else {
                    sender.sendMessage(rank.getFormattedName() + " Members (" + members.size() + "):");
                    for (String s : members) {
                        sender.sendMessage(ChatColor.GREEN + "- " + rank.getTagColor() + s);
                    }
                }
                return;
            }
        }
        helpMenu(sender);
    }

    private void helpMenu(CommandSender sender) {
        sender.sendMessage(ChatColor.GREEN + "/perm rank [rank] get [permission] " + ChatColor.AQUA + "- Get the value of a permission for a rank");
        sender.sendMessage(ChatColor.GREEN + "/perm rank [rank] set [permission] <true/false> " + ChatColor.AQUA + "- Set the value of a permission for a rank");
        sender.sendMessage(ChatColor.GREEN + "/perm rank [rank] unset [permission] " + ChatColor.AQUA + "- Remove the value of a permission for a rank");
        sender.sendMessage(ChatColor.GREEN + "/perm rank [rank] members " + ChatColor.AQUA + "- List the members of a rank");
    }
}

package network.palace.core.commands.permissions;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.RankTag;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import java.util.List;

@CommandMeta(description = "Tag commands")
public class TagCommand extends CoreCommand {

    public TagCommand() {
        super("tag");
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
        if (!args[1].equalsIgnoreCase("members")) {
            helpMenu(sender);
            return;
        }
        RankTag tag = RankTag.fromString(args[0]);

        List<String> members = Core.getMongoHandler().getMembers(tag);
        if (members == null) {
            sender.sendMessage(ChatColor.RED + "Too many members to list!");
        } else {
            sender.sendMessage(tag.getTag() + tag.getColor() + "Members (" + members.size() + "):");
            for (String s : members) {
                sender.sendMessage(ChatColor.GREEN + "- " + tag.getColor() + s);
            }
        }
    }

    private void helpMenu(CommandSender sender) {
        sender.sendMessage(ChatColor.GREEN + "/perm tag [tag] members " + ChatColor.AQUA + "- List players with a specific tag");
    }
}

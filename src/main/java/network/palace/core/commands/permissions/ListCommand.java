package network.palace.core.commands.permissions;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.Rank;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

@CommandMeta(description = "List ranks")
public class ListCommand extends CoreCommand {

    public ListCommand() {
        super("list");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        if (args.length == 0 || !args[0].equalsIgnoreCase("ranks")) {
            sender.sendMessage(ChatColor.GREEN + "- /perm list ranks " + ChatColor.AQUA + "- List ranks");
            return;
        }
        sender.sendMessage(ChatColor.LIGHT_PURPLE + "Palace Ranks:");
        sender.sendMessage(ChatColor.GREEN + "- id:dbname:display name");
        for (Rank rank : Rank.values()) {
            sender.sendMessage(ChatColor.GREEN + "- " + rank.getRankId() + ":" + rank.getDBName() + ":" + rank.getFormattedName());
        }
    }
}

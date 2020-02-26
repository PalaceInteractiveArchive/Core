package network.palace.core.commands.permissions;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.core.player.RankTag;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

@CommandMeta(description = "List ranks")
public class ListCommand extends CoreCommand {

    public ListCommand() {
        super("list");
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
        if (args.length == 0) {
            sender.sendMessage(ChatColor.GREEN + "- /perm list ranks " + ChatColor.AQUA + "- List ranks");
            sender.sendMessage(ChatColor.GREEN + "- /perm list tags " + ChatColor.AQUA + "- List tags");
            return;
        }
        switch (args[0].toLowerCase()) {
            case "ranks": {
                sender.sendMessage(ChatColor.LIGHT_PURPLE + "Palace Ranks:");
                sender.sendMessage(ChatColor.GREEN + "- id:dbname:display name");
                for (Rank rank : Rank.values()) {
                    sender.sendMessage(ChatColor.GREEN + "- " + rank.getRankId() + ":" + rank.getDBName() + ":" + rank.getFormattedName());
                }
                break;
            }
            case "tags": {
                sender.sendMessage(ChatColor.LIGHT_PURPLE + "Palace Sponsor Tiers:");
                sender.sendMessage(ChatColor.GREEN + "- dbname:chat tag:scoreboard tag");
                for (RankTag tag : RankTag.values()) {
                    sender.sendMessage(ChatColor.GREEN + "- " + tag.getDBName() + ":" + tag.getTag() + ":" + tag.getScoreboardTag());
                }
                break;
            }
        }
    }
}

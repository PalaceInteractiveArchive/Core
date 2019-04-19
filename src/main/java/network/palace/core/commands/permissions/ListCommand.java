package network.palace.core.commands.permissions;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.Rank;
import network.palace.core.player.SponsorTier;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

@CommandMeta(description = "List ranks")
public class ListCommand extends CoreCommand {

    public ListCommand() {
        super("list");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.GREEN + "- /perm list ranks " + ChatColor.AQUA + "- List ranks");
            sender.sendMessage(ChatColor.GREEN + "- /perm list tiers " + ChatColor.AQUA + "- List sponsor tiers");
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
            case "tiers": {
                sender.sendMessage(ChatColor.LIGHT_PURPLE + "Palace Sponsor Tiers:");
                sender.sendMessage(ChatColor.GREEN + "- dbname:chat tag:scoreboard tag");
                for (SponsorTier tier : SponsorTier.values()) {
                    if (tier.equals(SponsorTier.NONE)) continue;
                    sender.sendMessage(ChatColor.GREEN + "- " + tier.getDBName() + ":" + tier.getChatTag(false) + ":" + tier.getScoreboardTag());
                }
                break;
            }
        }
    }
}

package network.palace.core.commands.permissions;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.SponsorTier;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import java.util.List;

@CommandMeta(description = "Sponsor commands")
public class SponsorCommand extends CoreCommand {

    public SponsorCommand() {
        super("sponsor");
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
        SponsorTier tier = SponsorTier.fromString(args[0]);

        List<String> members = Core.getMongoHandler().getMembers(tier);
        if (members == null) {
            sender.sendMessage(ChatColor.RED + "Too many members to list!");
        } else {
            sender.sendMessage(tier.getChatTag(false) + " Members (" + members.size() + "):");
            for (String s : members) {
                sender.sendMessage(ChatColor.GREEN + "- " + tier.getColor() + s);
            }
        }
    }

    private void helpMenu(CommandSender sender) {
        sender.sendMessage(ChatColor.GREEN + "/perm sponsor [tier] members " + ChatColor.AQUA + "- List the members of a sponsor tier");
    }
}

package network.palace.core.commands;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CommandPermission;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.core.utils.MiscUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Innectic
 * @since 6/19/2017
 */
@CommandMeta(description = "Teleport a player", aliases = "tp")
@CommandPermission(rank = Rank.TRAINEE)
public class TeleportCommand extends CoreCommand {

    public TeleportCommand() {
        super("teleport");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        CPlayer player;
        if (args.length == 1 && sender instanceof Player) {
            player = Core.getPlayerManager().getPlayer(((Player) sender).getUniqueId());
            CPlayer to = Core.getPlayerManager().getPlayer(args[0]);
            if (to == null) {
                sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Invalid player.");
                return;
            }
            player.teleport(to.getLocation());
            sender.sendMessage(ChatColor.BLUE + "You teleported to " + to.getName());
        } else if (args.length == 2) {
            CPlayer to = Core.getPlayerManager().getPlayer(args[0]);
            CPlayer from = Core.getPlayerManager().getPlayer(args[1]);
            if (to == null || from == null) {
                sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Invalid player.");
                return;
            }
            to.teleport(from.getLocation());
            sender.sendMessage(ChatColor.BLUE + to.getName() + " teleported to " + from.getName());
        } else if (args.length == 4) {
            if (!MiscUtil.checkIfInt(args[1])) {
                sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Invalid location.");
                return;
            }
            if (!MiscUtil.checkIfInt(args[2])) {
                sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Invalid location.");
                return;
            }
            if (!MiscUtil.checkIfInt(args[3])) {
                sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Invalid location.");
                return;
            }
            CPlayer to = Core.getPlayerManager().getPlayer(args[0]);
            if (to == null) {
                sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Invalid player.");
                return;
            }
            to.teleport(new Location(to.getWorld(), Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3])));
            sender.sendMessage(ChatColor.BLUE + "You teleported " + to.getName() + " to " + args[1] + ", " + args[2] + ", " + args[3]);
        } else if (args.length == 3 && sender instanceof Player) {
            if (!MiscUtil.checkIfInt(args[0])) {
                sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Invalid location.");
                return;
            }
            if (!MiscUtil.checkIfInt(args[1])) {
                sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Invalid location.");
                return;
            }
            if (!MiscUtil.checkIfInt(args[2])) {
                sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Invalid location.");
                return;
            }
            player = Core.getPlayerManager().getPlayer(((Player) sender).getUniqueId());
            player.teleport(new Location(player.getWorld(), Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2])));
            sender.sendMessage(ChatColor.BLUE + "You teleported to " + args[0] + ", " + args[1] + ", " + args[2]);
        }
    }
}


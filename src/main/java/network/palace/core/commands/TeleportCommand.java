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

/**
 * @author Innectic
 * @since 6/19/2017
 */
@CommandPermission(rank = Rank.SQUIRE)
@CommandMeta(description = "Teleport a player", aliases = "tp")
public class TeleportCommand extends CoreCommand {

    public TeleportCommand() {
        super("teleport");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length == 1) {
            CPlayer to = Core.getPlayerManager().getPlayer(args[0]);
            if (to == null) {
                player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Invalid player.");
                return;
            }
            player.teleport(to.getLocation());
            player.sendMessage(ChatColor.BLUE + "You teleported to " + to.getName());
        } else if (args.length == 2) {
            CPlayer to = Core.getPlayerManager().getPlayer(args[0]);
            CPlayer from = Core.getPlayerManager().getPlayer(args[1]);
            if (to == null || from == null) {
                player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Invalid player.");
                return;
            }
            to.teleport(from.getLocation());
            player.sendMessage(ChatColor.BLUE + to.getName() + " teleported to " + from.getName());
        } else if (args.length == 4) {
            if (!MiscUtil.checkIfInt(args[1])) {
                player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Invalid location.");
                return;
            }
            if (!MiscUtil.checkIfInt(args[2])) {
                player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Invalid location.");
                return;
            }
            if (!MiscUtil.checkIfInt(args[3])) {
                player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Invalid location.");
                return;
            }
            CPlayer to = Core.getPlayerManager().getPlayer(args[0]);
            if (to == null) {
                player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Invalid player.");
                return;
            }
            to.teleport(new Location(to.getWorld(), Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3])));
            player.sendMessage(ChatColor.BLUE + "You teleported " + to.getName() + " to " + args[1] + ", " + args[2] + ", " + args[3]);
        } else if (args.length == 3) {
            if (!MiscUtil.checkIfInt(args[0])) {
                player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Invalid location.");
                return;
            }
            if (!MiscUtil.checkIfInt(args[1])) {
                player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Invalid location.");
                return;
            }
            if (!MiscUtil.checkIfInt(args[2])) {
                player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Invalid location.");
                return;
            }

            player.teleport(new Location(player.getWorld(), Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2])));
            player.sendMessage(ChatColor.BLUE + "You teleported to " + args[0] + ", " + args[1] + ", " + args[2]);
        }
    }
}

package network.palace.core.commands;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Innectic
 * @since 6/19/2017
 */
@CommandMeta(description = "Teleport a player", aliases = "tp", rank = Rank.TRAINEE)
public class TeleportCommand extends CoreCommand {

    public TeleportCommand() {
        super("teleport");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof Player)) {
            if (args.length == 2) {
                CPlayer tp1 = Core.getPlayerManager().getPlayer(args[0]);
                CPlayer tp2 = Core.getPlayerManager().getPlayer(args[1]);
                if (tp1 == null || tp2 == null) {
                    sender.sendMessage(ChatColor.RED + "Player not found!");
                    return;
                }
                if (tp1.isInsideVehicle()) {
                    sender.sendMessage(ChatColor.RED + tp1.getName() + " is in a vehicle, you can't teleport them!");
                    return;
                }
                if (tp2.isInsideVehicle() && !tp2.getGamemode().equals(GameMode.SPECTATOR)) {
                    sender.sendMessage(ChatColor.RED + tp2.getName() + " is in a vehicle, you can't teleport to them! " +
                            "They must be in Spectator Mode to teleport to players on rides.");
                    return;
                }
                tp1.teleport(tp2);
                sender.sendMessage(ChatColor.GRAY + tp1.getName() + " has been teleported to " + tp2.getName());
                return;
            }
            if (args.length == 4) {
                try {
                    CPlayer tp = Core.getPlayerManager().getPlayer(args[0]);
                    double x = args[1].startsWith("~") ? tp.getLocation().getX() + num(args[1].substring(1)) : num(args[1]);
                    double y = args[2].startsWith("~") ? tp.getLocation().getY() + num(args[2].substring(1)) : num(args[2]);
                    double z = args[3].startsWith("~") ? tp.getLocation().getZ() + num(args[3].substring(1)) : num(args[3]);
                    Location loc = new Location(tp.getWorld(), x, y, z, tp
                            .getLocation().getYaw(), tp.getLocation().getPitch());
                    if (tp.isInsideVehicle()) {
                        sender.sendMessage(ChatColor.RED + tp.getName() + " is in a vehicle, you can't teleport them!");
                        return;
                    }
                    tp.teleport(loc);
                    sender.sendMessage(ChatColor.GRAY + tp.getName() + " has been teleported to " + x + ", " + y + ", "
                            + z);
                    return;
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "Error with numbers!");
                    return;
                }
            }
            sender.sendMessage(ChatColor.RED + "/tp [Player] <Target> or <x> <y> <z>");
            return;
        }
        CPlayer player = Core.getPlayerManager().getPlayer((Player) sender);
        if (args.length == 1) {
            CPlayer tp = Core.getPlayerManager().getPlayer(args[0]);
            if (tp == null) {
                player.sendMessage(ChatColor.RED + "Player not found!");
                return;
            }
            if (tp.isInsideVehicle() && !player.getUniqueId().equals(tp.getUniqueId()) && !player.getGamemode().equals(GameMode.SPECTATOR)) {
                sender.sendMessage(ChatColor.RED + tp.getName() + " is in a vehicle, you can't teleport to them! " +
                        "You must be in Spectator Mode to teleport to players on rides.");
                return;
            }
            player.teleport(tp);
            player.sendMessage(ChatColor.GRAY + "You teleported to " + tp.getName());
            return;
        }
        if (args.length == 2) {
            CPlayer tp1 = Core.getPlayerManager().getPlayer(args[0]);
            CPlayer tp2 = Core.getPlayerManager().getPlayer(args[1]);
            if (tp1 == null || tp2 == null) {
                sender.sendMessage(ChatColor.RED + "Player not found!");
                return;
            }
            if (tp1.isInsideVehicle()) {
                sender.sendMessage(ChatColor.RED + tp1.getName() + " is in a vehicle, you can't teleport them!");
                return;
            }
            if (tp2.isInsideVehicle() && !tp2.getGamemode().equals(GameMode.SPECTATOR)) {
                sender.sendMessage(ChatColor.RED + tp2.getName() + " is in a vehicle, you can't teleport to them!");
                return;
            }
            tp1.teleport(tp2);
            player.sendMessage(ChatColor.GRAY + tp1.getName()
                    + " has been teleported to " + tp2.getName());
            return;
        }
        if (args.length == 3) {
            try {
                double x = args[0].startsWith("~") ? player.getLocation().getX() + num(args[0].substring(1)) : num(args[0]);
                double y = args[1].startsWith("~") ? player.getLocation().getY() + num(args[1].substring(1)) : num(args[1]);
                double z = args[2].startsWith("~") ? player.getLocation().getZ() + num(args[2].substring(1)) : num(args[2]);
                Location loc = new Location(player.getWorld(), x, y, z, player.getLocation().getYaw(), player.getLocation().getPitch());
                player.teleport(loc);
                player.sendMessage(ChatColor.GRAY + "You teleported to " + x + ", " + y + ", " + z);
                return;
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Error with numbers!");
                return;
            }
        }
        if (args.length == 4) {
            try {
                CPlayer tp = Core.getPlayerManager().getPlayer(args[0]);
                double x = args[1].startsWith("~") ? player.getLocation().getX() + num(args[1].substring(1)) : num(args[1]);
                double y = args[2].startsWith("~") ? player.getLocation().getY() + num(args[2].substring(1)) : num(args[2]);
                double z = args[3].startsWith("~") ? player.getLocation().getZ() + num(args[3].substring(1)) : num(args[3]);
                Location loc = new Location(tp.getWorld(), x, y, z, player.getLocation().getYaw(), player.getLocation().getPitch());
                if (tp.isInsideVehicle()) {
                    sender.sendMessage(ChatColor.RED + tp.getName() + " is in a vehicle, you can't teleport to them!");
                    return;
                }
                tp.teleport(loc);
                player.sendMessage(ChatColor.GRAY + tp.getName() + " has been teleported to " + x + ", " + y + ", " + z);
                return;
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Error with numbers!");
                return;
            }
        }
        player.sendMessage(ChatColor.RED + "/tp [Player] <Target> or /tp <x> <y> <z> or /tp [Player] <x> <y> <z>");
    }

    private double num(String s) {
        if (s == null) {
            return 0;
        }
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException ignored) {
            return 0;
        }
    }
}


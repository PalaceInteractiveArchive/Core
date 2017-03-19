package network.palace.core.commands;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CommandPermission;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.Rank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * The type Balance command.
 */
@CommandMeta(aliases = "bal", description = "Manage economy balances")
@CommandPermission(rank = Rank.KNIGHT)
public class BalanceCommand extends CoreCommand {

    /**
     * Instantiates a new Balance command.
     */
    public BalanceCommand() {
        super("balance");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        boolean isPlayer = sender instanceof Player;
        if (args.length == 0) {
            if (isPlayer) {
                Core.runTaskAsynchronously(() -> sender.sendMessage(ChatColor.YELLOW +
                        "" + ChatColor.BOLD + "Your Balance: " + ChatColor.GREEN + "$" +
                        Core.getEconomy().getBalance(((Player) sender).getUniqueId())));
            } else {
                helpMenu(sender);
            }
            return;
        }
        if (args.length == 1) {
            final String user = args[0];
            Core.runTaskAsynchronously(() -> sender.sendMessage(ChatColor.YELLOW +
                    "" + ChatColor.BOLD + "Balance for " + user + ": " + ChatColor.GREEN + "$" +
                    Core.getEconomy().getBalance(sender, user)));
            return;
        }
        if (args.length == 2) {
            if (!isPlayer) {
                helpMenu(sender);
            } else {
                String action = args[0];
                Player tp = (Player) sender;
                if (!isInt(args[1])) {
                    helpMenu(sender);
                    return;
                }
                String source = tp.getName();
                int amount = Integer.parseInt(args[1]);
                switch (action.toLowerCase()) {
                    case "set":
                        Core.getEconomy().setBalance(tp.getUniqueId(), amount, source, true);
                        break;
                    case "add":
                        Core.getEconomy().addBalance(tp.getUniqueId(), amount, source);
                        break;
                    case "minus":
                        Core.getEconomy().addBalance(tp.getUniqueId(), -amount, source);
                        break;
                }
            }
            return;
        }
        if (args.length == 3) {
            String action = args[0];
            Player tp = Bukkit.getPlayer(args[2]);
            if (tp == null) {
                sender.sendMessage(ChatColor.GREEN + args[2] + ChatColor.RED + " is not online!");
                return;
            }
            if (!isInt(args[1])) {
                helpMenu(sender);
                return;
            }
            String source;
            if (sender instanceof BlockCommandSender) {
                BlockCommandSender s = (BlockCommandSender) sender;
                Location loc = s.getBlock().getLocation();
                source = "Command Block x: " + loc.getBlockX() + " y: " + loc.getBlockY() + " z: " + loc.getBlockZ();
            } else {
                source = sender instanceof Player ? sender.getName() : "Console";
            }
            int amount = Integer.parseInt(args[1]);
            switch (action.toLowerCase()) {
                case "set":
                    Core.getEconomy().setBalance(tp.getUniqueId(), amount, source, true);
                    break;
                case "add":
                    Core.getEconomy().addBalance(tp.getUniqueId(), amount, source);
                    break;
                case "minus":
                    Core.getEconomy().addBalance(tp.getUniqueId(), -amount, source);
                    break;
            }
            return;
        }
        helpMenu(sender);
    }

    private boolean isInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException ignored) {
            return false;
        }
    }

    private void helpMenu(CommandSender sender) {
        sender.sendMessage(ChatColor.YELLOW
                + "/balance <player> - Gets the amount of coins a player has.");
        sender.sendMessage(ChatColor.YELLOW
                + "/balance <set,add,minus> <amount> [player] - Changes the amount of coins a player has.");
    }
}

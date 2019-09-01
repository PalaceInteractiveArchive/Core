package network.palace.core.commands;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.economy.CurrencyType;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.core.utils.MiscUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * The type Balance command.
 */
@CommandMeta(aliases = "bal", description = "Manage economy balances", rank = Rank.MOD)
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
                Core.runTaskAsynchronously(Core.getInstance(), () -> sender.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD +
                        "Your Balance: " + ChatColor.GREEN + "$" +
                        Core.getMongoHandler().getCurrency(((Player) sender).getUniqueId(), CurrencyType.BALANCE)));
            } else {
                helpMenu(sender);
            }
            return;
        }
        if (args.length == 1) {
            final String user = args[0];
            Core.runTaskAsynchronously(Core.getInstance(), () -> {
                UUID uuid = Core.getMongoHandler().usernameToUUID(user);
                if (uuid != null) {
                    sender.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "Balance for " + user + ": " +
                            ChatColor.GREEN + "$" + Core.getMongoHandler().getCurrency(uuid, CurrencyType.BALANCE));
                } else {
                    sender.sendMessage(ChatColor.RED + "Player not found!");
                }
            });
            return;
        }
        if (args.length == 2) {
            if (!isPlayer) {
                helpMenu(sender);
            } else {
                String action = args[0];
                CPlayer tp = Core.getPlayerManager().getPlayer(((Player) sender).getUniqueId());
                if (tp == null) {
                    sender.sendMessage(ChatColor.RED + "Player not found!");
                    return;
                }
                if (!MiscUtil.checkIfInt(args[1])) {
                    helpMenu(sender);
                    return;
                }
                String source = tp.getName();
                int amount = Integer.parseInt(args[1]);
                Core.runTaskAsynchronously(Core.getInstance(), () -> {
                    switch (action.toLowerCase()) {
                        case "set":
                            Core.getMongoHandler().changeAmount(tp.getUniqueId(), amount, source, CurrencyType.BALANCE, true);
                            break;
                        case "add":
                            tp.getActionBar().show(ChatColor.GREEN + "+" + CurrencyType.BALANCE.getIcon() + amount);
                            Core.getMongoHandler().changeAmount(tp.getUniqueId(), amount, source, CurrencyType.BALANCE, false);
                            break;
                        case "minus":
                            tp.getActionBar().show(ChatColor.GREEN + "-" + CurrencyType.BALANCE.getIcon() + amount);
                            Core.getMongoHandler().changeAmount(tp.getUniqueId(), -amount, source, CurrencyType.BALANCE, false);
                            break;
                    }
                });
            }
            return;
        }
        if (args.length == 3) {
            String action = args[0];
            CPlayer tp = Core.getPlayerManager().getPlayer(Bukkit.getPlayer(args[2]));
            if (tp == null) {
                sender.sendMessage(ChatColor.GREEN + args[2] + ChatColor.RED + " is not online!");
                return;
            }
            if (!MiscUtil.checkIfInt(args[1])) {
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
            Core.runTaskAsynchronously(Core.getInstance(), () -> {
                switch (action.toLowerCase()) {
                    case "set":
                        Core.getMongoHandler().changeAmount(tp.getUniqueId(), amount, source, CurrencyType.BALANCE, true);
                        break;
                    case "add":
                        tp.getActionBar().show(ChatColor.GREEN + "+" + CurrencyType.BALANCE.getIcon() + amount);
                        Core.getMongoHandler().changeAmount(tp.getUniqueId(), amount, source, CurrencyType.BALANCE, false);
                        break;
                    case "minus":
                        tp.getActionBar().show(ChatColor.GREEN + "-" + CurrencyType.BALANCE.getIcon() + amount);
                        Core.getMongoHandler().changeAmount(tp.getUniqueId(), -amount, source, CurrencyType.BALANCE, false);
                        break;
                }
            });
            return;
        }
        helpMenu(sender);
    }

    private void helpMenu(CommandSender sender) {
        sender.sendMessage(ChatColor.YELLOW
                + "/balance <player> - Gets the amount of coins a player has.");
        sender.sendMessage(ChatColor.YELLOW
                + "/balance <set,add,minus> <amount> [player] - Changes the amount of coins a player has.");
    }
}

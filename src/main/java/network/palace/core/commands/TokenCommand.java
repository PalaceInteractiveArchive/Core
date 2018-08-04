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
 * The type Token command.
 */
@CommandMeta(description = "Manage economy tokens", rank = Rank.MOD)
public class TokenCommand extends CoreCommand {

    /**
     * Instantiates a new Token command.
     */
    public TokenCommand() {
        super("token");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        boolean isPlayer = sender instanceof Player;
        if (args.length == 0) {
            if (isPlayer) {
                Core.runTaskAsynchronously(() -> sender.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD +
                        "Your Tokens: " + ChatColor.GREEN + "✪ " +
                        Core.getMongoHandler().getCurrency(((Player) sender).getUniqueId(), CurrencyType.TOKENS)));
            } else {
                helpMenu(sender);
            }
            return;
        }
        if (args.length == 1) {
            final String user = args[0];
            Core.runTaskAsynchronously(() -> {
                UUID uuid = Core.getMongoHandler().usernameToUUID(user);
                if (uuid != null) {
                    sender.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "Tokens for " + user + ": " +
                            ChatColor.GREEN + "✪ " + Core.getMongoHandler().getCurrency(uuid, CurrencyType.TOKENS));
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
                Core.runTaskAsynchronously(() -> {
                    switch (action.toLowerCase()) {
                        case "set":
                            Core.getMongoHandler().changeAmount(tp.getUniqueId(), amount, source, CurrencyType.TOKENS, true);
                            break;
                        case "add":
                            tp.getActionBar().show(ChatColor.YELLOW + "+" + CurrencyType.TOKENS.getIcon() + amount);
                            Core.getMongoHandler().changeAmount(tp.getUniqueId(), amount, source, CurrencyType.TOKENS, false);
                            break;
                        case "minus":
                            tp.getActionBar().show(ChatColor.YELLOW + "-" + CurrencyType.TOKENS.getIcon() + amount);
                            Core.getMongoHandler().changeAmount(tp.getUniqueId(), -amount, source, CurrencyType.TOKENS, false);
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
            Core.runTaskAsynchronously(() -> {
                switch (action.toLowerCase()) {
                    case "set":
                        Core.getMongoHandler().changeAmount(tp.getUniqueId(), amount, source, CurrencyType.TOKENS, true);
                        break;
                    case "add":
                        tp.getActionBar().show(ChatColor.YELLOW + "+" + CurrencyType.TOKENS.getIcon() + amount);
                        Core.getMongoHandler().changeAmount(tp.getUniqueId(), amount, source, CurrencyType.TOKENS, false);
                        break;
                    case "minus":
                        tp.getActionBar().show(ChatColor.YELLOW + "-" + CurrencyType.TOKENS.getIcon() + amount);
                        Core.getMongoHandler().changeAmount(tp.getUniqueId(), -amount, source, CurrencyType.TOKENS, false);
                        break;
                }
            });
            return;
        }
        helpMenu(sender);
    }

    private void helpMenu(CommandSender sender) {
        sender.sendMessage(ChatColor.YELLOW
                + "/tokens <player> - Gets the amount of tokens a player has.");
        sender.sendMessage(ChatColor.YELLOW
                + "/tokens <set,add,minus> <amount> [player] - Changes the amount of tokens a player has.");
    }
}

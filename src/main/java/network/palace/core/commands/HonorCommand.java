package network.palace.core.commands;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.economy.honor.HonorMapping;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.core.utils.MiscUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.UUID;

/**
 * @author Marc
 * @since 6/18/17
 */
@CommandMeta(description = "Get your current honor count and level", rank = Rank.DEVELOPER)
public class HonorCommand extends CoreCommand {
    private final DecimalFormat format = new DecimalFormat("#,###");

    public HonorCommand() {
        super("honor");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        boolean isPlayer = sender instanceof Player;
        if (args.length == 0) {
            helpMenu(sender);
            return;
        }
        if (args.length == 1 && isPlayer) {
            final String user = args[0];
            Core.runTaskAsynchronously(Core.getInstance(), () -> {
                UUID uuid = Core.getMongoHandler().usernameToUUID(user);
                if (uuid == null) {
                    sender.sendMessage(ChatColor.RED + "Player not found");
                    return;
                }
                int honor = Core.getMongoHandler().getHonor(uuid);
                int level = Core.getHonorManager().getLevel(honor).getLevel();
                HonorMapping nextLevel = Core.getHonorManager().getNextLevel(honor);
                float progress = Core.getHonorManager().progressToNextLevel(honor);
                sender.sendMessage(ChatColor.GREEN + args[0] + " is Level " + format(level) + " with " + format(honor) +
                        " Honor.");
                if (level < Core.getHonorManager().getTopLevel()) {
                    sender.sendMessage(ChatColor.GREEN + "They are " + (nextLevel.getHonor() - honor) + " Honor (" +
                            (int) (progress * 100.0f) + "%) away from Level " + nextLevel.getLevel());
                } else {
                    sender.sendMessage(ChatColor.GREEN + "They are at the highest level.");
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
                process(tp, Integer.parseInt(args[1]), tp.getName(), action);
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
            process(tp, Integer.parseInt(args[1]), source, action);
            return;
        }
        helpMenu(sender);
    }

    private void process(CPlayer player, int amount, String source, String action) {
        switch (action.toLowerCase()) {
            case "set":
                player.setHonor(amount, source);
                break;
            case "add":
                player.giveHonor(amount, source);
                break;
            case "minus":
                player.giveHonor(-amount, source);
                break;
        }
    }

    private void helpMenu(CommandSender sender) {
        sender.sendMessage(ChatColor.YELLOW
                + "/honor [player] - Gets the amount of honor a player has.");
        sender.sendMessage(ChatColor.YELLOW
                + "/honor [set,add,minus] [amount] <player> - Changes the amount of honor a player has.");
    }

    private String format(int i) {
        return format.format(i);
    }
}

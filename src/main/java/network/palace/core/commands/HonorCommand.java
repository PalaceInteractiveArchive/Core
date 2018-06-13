package network.palace.core.commands;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.honor.HonorMapping;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import org.bukkit.ChatColor;
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
        Core.runTaskAsynchronously(() -> {
            if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
                helpMenu(sender);
                return;
            }
            if (args.length > 1) {
                int amount;
                try {
                    amount = Integer.parseInt(args[1]);
                } catch (NumberFormatException ignored) {
                    sender.sendMessage(ChatColor.RED + "Invalid number " + args[1]);
                    return;
                }
                UUID uuid;
                String name;
                if (args.length > 2) {
                    uuid = Core.getMongoHandler().usernameToUUID(args[2]);
                    name = args[2];
                } else {
                    uuid = ((Player) sender).getUniqueId();
                    name = sender.getName();
                }
                if (uuid == null) {
                    sender.sendMessage(ChatColor.RED + "Player not found!");
                    return;
                }
                CPlayer player = Core.getPlayerManager().getPlayer(uuid);
                switch (args[0]) {
                    case "add":
                        if (player == null) {
                            Core.getMongoHandler().addHonor(player.getUniqueId(), amount);
                        } else {
                            player.giveHonor(amount);
                        }
                        sender.sendMessage(ChatColor.GREEN + "Added " + amount + " to " + name + "'s Honor");
                        return;
                    case "minus":
                        if (player == null) {
                            Core.getMongoHandler().addHonor(player.getUniqueId(), -amount);
                        } else {
                            player.removeHonor(amount);
                        }
                        sender.sendMessage(ChatColor.GREEN + "Removed " + amount + " from " + name + "'s Honor");
                        return;
                    case "set":
                        Core.getMongoHandler().setHonor(uuid, amount);
                        if (player != null) {
                            player.setHonor(amount);
                            player.getActionBar().show(ChatColor.LIGHT_PURPLE + "Your Honor was set to " + amount);
                            Core.getHonorManager().displayHonor(player);
                        }
                        sender.sendMessage(ChatColor.GREEN + "Set " + name + "'s Honor to " + amount);
                        return;
                }
                helpMenu(sender);
                return;
            }
            UUID uuid = Core.getMongoHandler().usernameToUUID(args[0]);
            if (uuid == null) {
                sender.sendMessage(ChatColor.RED + "Player not found!");
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
    }

    private void helpMenu(CommandSender sender) {
        sender.sendMessage(ChatColor.YELLOW + "/honor [player] - Gets the honor report of a player.");
        sender.sendMessage(ChatColor.YELLOW
                + "/honor [set,add,minus] [amount] <player> - Changes the amount of honor a player has.");
    }

    private String format(int i) {
        return format.format(i);
    }
}

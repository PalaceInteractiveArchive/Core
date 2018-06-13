package network.palace.core.commands;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import org.bukkit.ChatColor;

@CommandMeta(description = "Toggles player flight", rank = Rank.TRAINEE)
public class FlyCommand extends CoreCommand {

    public FlyCommand() {
        super("fly");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        switch (args.length) {
            case 0: {
                player.setAllowFlight(!player.getAllowFlight());
                player.setFlying(player.getAllowFlight());
                message(player);
                break;
            }
            case 1: {
                if (!args[0].equalsIgnoreCase("help")) {
                    CPlayer target = Core.getPlayerManager().getPlayer(args[0]);
                    if (target == null) {
                        player.sendMessage(ChatColor.RED + "Player not found!");
                        return;
                    }
                    target.setAllowFlight(!target.getAllowFlight());
                    message(player, target);
                    message(target);
                    break;
                }
            }
            default: {
                player.sendMessage(ChatColor.GREEN + "Flight Commands:");
                player.sendMessage(ChatColor.GREEN + "/fly " + ChatColor.AQUA + "- Toggle your own flight ability");
                player.sendMessage(ChatColor.GREEN + "/fly [Username] " + ChatColor.AQUA + "- Toggle another player's flight ability");
                player.sendMessage(ChatColor.GREEN + "/fly help " + ChatColor.AQUA + "- Show this help menu");
            }
        }
    }

    private void message(CPlayer player) {
        message(player, player);
    }

    /**
     * Tell a player if another player can fly
     *
     * @param player the player to message
     * @param about  the player that can/can't fly
     */
    private void message(CPlayer player, CPlayer about) {
        String target = player.getUniqueId().equals(about.getUniqueId()) ? "You" : about.getName();
        if (about.getAllowFlight()) {
            player.sendMessage(ChatColor.GREEN + target + " can now fly!");
        } else {
            player.sendMessage(ChatColor.RED + target + " can no longer fly!");
        }
    }
}

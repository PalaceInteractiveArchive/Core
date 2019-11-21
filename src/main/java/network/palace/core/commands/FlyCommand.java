package network.palace.core.commands;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.events.PlayerToggleAllowFlightEvent;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

@CommandMeta(description = "Toggle player flight", rank = Rank.SHAREHOLDER)
public class FlyCommand extends CoreCommand {

    public FlyCommand() {
        super("fly");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length < 1 || player.getRank().getRankId() < Rank.TRAINEE.getRankId()) {
            toggleFlight(player, player);
            return;
        }
        CPlayer target = Core.getPlayerManager().getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(ChatColor.RED + "Player not found!");
            return;
        }
        toggleFlight(player, target);
    }

    private void toggleFlight(CPlayer sender, CPlayer target) {
        if (sender.getRank().equals(Rank.SHAREHOLDER) && Bukkit.getPluginManager().getPlugin("ParkManager") == null) {
            // only allow shareholders to fly on park servers
            sender.sendMessage(ChatColor.RED + "You can only use this on Park servers!");
            return;
        }
        // call event to allow Vanish to hide the player if needed
        new PlayerToggleAllowFlightEvent(sender, !target.getAllowFlight()).call();
        if (target.getAllowFlight()) {
            target.setAllowFlight(false);
            target.sendMessage(ChatColor.RED + "You can no longer fly!");
            if (!sender.getUniqueId().equals(target.getUniqueId()))
                sender.sendMessage(ChatColor.RED + target.getName() + " can no longer fly!");
        } else {
            target.setAllowFlight(true);
            target.sendMessage(ChatColor.GREEN + "You can now fly!");
            if (!sender.getUniqueId().equals(target.getUniqueId()))
                sender.sendMessage(ChatColor.GREEN + target.getName() + " can now fly!");
        }
    }
}

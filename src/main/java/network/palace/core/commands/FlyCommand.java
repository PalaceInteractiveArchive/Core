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

import java.io.File;
import java.io.IOException;

@CommandMeta(description = "Toggle player flight", rank = Rank.SHAREHOLDER)
public class FlyCommand extends CoreCommand {
    private boolean shareholderFlightDisabled;

    public FlyCommand() {
        super("fly");
        shareholderFlightDisabled = Core.getCoreConfig().getBoolean("shareholderFlightDisabled", false);
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length < 1 || player.getRank().getRankId() < Rank.TRAINEE.getRankId()) {
            toggleFlight(player, player);
            return;
        }
        if (args[0].equalsIgnoreCase("sharetoggle") && Bukkit.getPluginManager().getPlugin("ParkManager") != null) {
            shareholderFlightDisabled = !shareholderFlightDisabled;
            player.sendMessage((shareholderFlightDisabled ? ChatColor.RED : ChatColor.GREEN) + "Shareholder flight has been " +
                    (shareholderFlightDisabled ? "disabled" : "enabled") + " for this server.");
            Core.getCoreConfig().set("shareholderFlightDisabled", shareholderFlightDisabled);
            try {
                Core.getCoreConfig().save(new File("plugins/Core/config.yml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
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
        if (sender.getRank().equals(Rank.SHAREHOLDER)) {
            if (Bukkit.getPluginManager().getPlugin("ParkManager") == null) {
                if (Bukkit.getPluginManager().getPlugin("Lobby") == null) {
                    // since ParkManager isn't present, isn't a park server, don't let shareholders fly
                    sender.sendMessage(ChatColor.RED + "You can only use this on Park servers!");
                    return;
                }
            }
            if (shareholderFlightDisabled) {
                sender.sendMessage(ChatColor.RED + "This command is disabled on this server!");
                return;
            }
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

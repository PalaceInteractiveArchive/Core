package network.palace.core.commands;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.ConsoleCommandSender;

/**
 * The type Helpop command.
 */
@CommandMeta(aliases = "ac", description = "Staff Chat command", rank = Rank.TRAINEE)
public class HelpopCommand extends CoreCommand {

    /**
     * Instantiates a new Helpop command.
     */
    public HelpopCommand() {
        super("helpop");
    }

    @Override
    protected void handleCommand(ConsoleCommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "/ac [Message]");
            return;
        }
        StringBuilder msg = new StringBuilder();
        for (String s : args) {
            msg.append(s).append(" ");
        }
        message("Console", msg.toString());
    }

    @Override
    protected void handleCommand(BlockCommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "/ac [Message]");
            return;
        }
        StringBuilder msg = new StringBuilder();
        for (String s : args) {
            msg.append(s).append(" ");
        }
        Location loc = sender.getBlock().getLocation();
        message("CB (x:" + loc.getBlockX() + " y:" + loc.getBlockY() + " z:" + loc.getBlockZ() + ")", msg.toString());
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "/ac [Message]");
            return;
        }
        StringBuilder msg = new StringBuilder();
        for (String s : args) {
            msg.append(s).append(" ");
        }
        message(player.getName(), msg.toString());
    }

    private void message(String sender, String message) {
        String msg = ChatColor.DARK_RED + "[CM CHAT] " + ChatColor.GRAY + sender + ": " + ChatColor.WHITE +
                ChatColor.translateAlternateColorCodes('&', message);
        for (CPlayer tp : Core.getPlayerManager().getOnlinePlayers()) {
            if (tp.getRank().getRankId() >= Rank.TRAINEE.getRankId()) {
                tp.sendMessage(msg);
            }
        }
        Bukkit.getLogger().info(msg);
    }
}

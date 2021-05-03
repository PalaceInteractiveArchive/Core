package network.palace.core.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.ChatColor;

/**
 * @author Tourist457
 * @since 4/25/2021
 */

@CommandMeta(description = "Kill entities safely", rank = Rank.CM)
public class KillCommand extends CoreCommand {

    public KillCommand() {
        super("kill");
    }

    @Override
    protected void handleCommand(ConsoleCommandSender sender, String[] args) throws CommandException {
        sender.sendMessage(ChatColor.RED + "You cannot kill entities in console!");
    }

    @Override
    protected void handleCommand(CPlayer sender, String[] args) throws CommandException {
        String command = String.join(" ", args);
        if (!command.matches(".*r=[1-9].*")) {
            sender.sendMessage(ChatColor.RED + "You cannot kill entities without a radius!");
            return;
        }
        sender.performCommand("minecraft:kill " + command);
    }
}
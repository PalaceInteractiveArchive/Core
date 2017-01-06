package network.palace.core.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Created by Marc on 1/6/17.
 */
@CommandMeta(description = "Disable me command")
public class MeCommand extends CoreCommand {

    public MeCommand() {
        super("me");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        sender.sendMessage(ChatColor.RED + "Disabled");
    }
}

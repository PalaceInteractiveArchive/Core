package network.palace.core.commands.disabled;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * @author Innectic
 * @since 1/26/2017
 */
@CommandMeta(description = "Disable stop command")
public class StopCommand extends CoreCommand {

    public StopCommand() {
        super("stop");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        sender.sendMessage(ChatColor.RED + "Disabled");
    }
}

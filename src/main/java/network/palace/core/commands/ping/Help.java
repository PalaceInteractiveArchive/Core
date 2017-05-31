package network.palace.core.commands.ping;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import org.bukkit.ChatColor;

/**
 * @author Innectic
 * @since 5/28/2017
 */
@CommandMeta(description = "Get help about your ping")
public class Help extends CoreCommand {

    public Help() {
        super("help");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        player.sendMessage(ChatColor.YELLOW + "" +
                ChatColor.BOLD + "Ping is the time in milliseconds that it takes for information to get from you, to our servers." +
        "The lower ping you have, the better experience you will have.");
    }
}

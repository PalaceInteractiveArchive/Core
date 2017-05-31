package network.palace.core.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.commands.ping.Help;
import network.palace.core.player.CPlayer;
import org.bukkit.ChatColor;

/**
 * Created by Marc on 2/9/17.
 */
@CommandMeta(description = "Tell players their current ping with the server")
public class PingCommand extends CoreCommand {

    public PingCommand() {
        super("ping");

        registerSubCommand(new Help());
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        int ping = player.getPing();
        if (ping < 0) {
            player.sendMessage(ChatColor.RED + "We're having an issue calculating your ping right now, try again in a few minutes!");
            return;
        }
        ChatColor color = getColor(ping);
        player.sendMessage(ChatColor.GOLD + "Your ping with our server is " + color + ping + " milliseconds. " +
                ChatColor.GOLD + "Type /ping help to learn more about your ping.");
    }

    private ChatColor getColor(int ping) {
        if (ping < 100) {
            return ChatColor.GREEN;
        } else if (ping < 350) {
            return ChatColor.YELLOW;
        } else {
            return ChatColor.RED;
        }
    }
}

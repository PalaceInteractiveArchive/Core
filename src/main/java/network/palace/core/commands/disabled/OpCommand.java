package network.palace.core.commands.disabled;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CommandPermission;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import org.bukkit.ChatColor;

/**
 * @author Innectic
 * @since 1/27/2017
 */
@CommandMeta(description = "Op players")
@CommandPermission(rank = Rank.WIZARD)
public class OpCommand extends CoreCommand {

    public OpCommand() {
        super("op");
    }

    @Override
    public void handleCommand(CPlayer player, String[] args) throws CommandException {
        player.sendMessage(ChatColor.RED + "Disabled.");
    }
}

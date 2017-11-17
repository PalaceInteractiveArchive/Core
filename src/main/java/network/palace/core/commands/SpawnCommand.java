package network.palace.core.commands;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CommandPermission;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import org.bukkit.ChatColor;

/**
 * @author Innectic
 * @since 5/23/2017
 */
@CommandMeta(description = "Get back to the world spawn")
@CommandPermission(rank = Rank.TRAINEE)
public class SpawnCommand extends CoreCommand {

    public SpawnCommand() {
        super("spawn");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        player.teleport(Core.getDefaultWorld().getSpawnLocation());
        player.sendMessage(ChatColor.GRAY + "Teleported you to the spawn.");
    }
}

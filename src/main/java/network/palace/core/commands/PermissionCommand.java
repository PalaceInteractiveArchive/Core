package network.palace.core.commands;

import network.palace.core.command.CommandMeta;
import network.palace.core.command.CommandPermission;
import network.palace.core.command.CoreCommand;
import network.palace.core.commands.permissions.ListCommand;
import network.palace.core.commands.permissions.PlayerCommand;
import network.palace.core.commands.permissions.RankCommand;
import network.palace.core.commands.permissions.RefreshCommand;
import network.palace.core.player.Rank;

/**
 * The type Perm command.
 */
@CommandMeta(description = "Permissions command")
@CommandPermission(rank = Rank.SRMOD)
public class PermissionCommand extends CoreCommand {

    /**
     * Instantiates a new Perm command.
     */
    public PermissionCommand() {
        super("perm");
        registerSubCommand(new ListCommand());
        registerSubCommand(new RankCommand());
        registerSubCommand(new RefreshCommand());
        registerSubCommand(new PlayerCommand());
    }

    @Override
    protected boolean isUsingSubCommandsOnly() {
        return true;
    }
}

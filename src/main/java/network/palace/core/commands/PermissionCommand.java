package network.palace.core.commands;

import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.commands.permissions.ListCommand;
import network.palace.core.commands.permissions.*;
import network.palace.core.player.Rank;

/**
 * The type Perm command.
 */
@CommandMeta(description = "Permissions command", rank = Rank.SRMOD)
public class PermissionCommand extends CoreCommand {

    /**
     * Instantiates a new Perm command.
     */
    public PermissionCommand() {
        super("perm");
        registerSubCommand(new ListCommand());
        registerSubCommand(new PlayerCommand());
        registerSubCommand(new RankCommand());
        registerSubCommand(new RefreshCommand());
        registerSubCommand(new SponsorCommand());
    }

    @Override
    protected boolean isUsingSubCommandsOnly() {
        return true;
    }
}

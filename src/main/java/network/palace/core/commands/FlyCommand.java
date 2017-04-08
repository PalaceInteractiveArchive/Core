package network.palace.core.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;

/**
 * @author Innectic
 * @since 4/8/2017
 */
public class FlyCommand extends CoreCommand {

    public FlyCommand() {
        super("fly");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        player.setAllowFlight(true);
        player.setFlying(true);
    }
}

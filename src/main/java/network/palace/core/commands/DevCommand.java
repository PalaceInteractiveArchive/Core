package network.palace.core.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;

@CommandMeta(rank = Rank.DEVELOPER)
public class DevCommand extends CoreCommand {

    public DevCommand() {
        super("tomsTesting");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        player.addAdventureCoins(Integer.parseInt(args[1]), "Dev Command");
    }
}

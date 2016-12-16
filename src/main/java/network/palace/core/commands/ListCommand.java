package network.palace.core.commands;

import com.google.common.base.Joiner;
import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CommandPermission;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@CommandMeta(aliases = {"who"}, description = "Lists all players on the server.")
@CommandPermission(rank = Rank.SQUIRE)
public class ListCommand extends CoreCommand {

    public ListCommand() {
        super("list");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        List<String> playerNames = Core.getPlayerManager().getOnlinePlayers().stream().map(CPlayer::getName).collect(Collectors.toList());
        Collections.sort(playerNames);
        String playerList = Joiner.on(" ").skipNulls().join(playerNames);
        // Formatter
        String playersOnlineFormat = Core.getLanguageFormatter().getFormat(sender, "command.list.playersOnline").replaceAll("<players-online>", playerList);
        sender.sendMessage(playersOnlineFormat);
    }
}

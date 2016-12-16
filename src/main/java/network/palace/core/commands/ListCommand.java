package com.palacemc.core.commands;

import com.google.common.base.Joiner;
import com.palacemc.core.Core;
import com.palacemc.core.command.CommandException;
import com.palacemc.core.command.CommandMeta;
import com.palacemc.core.command.CommandPermission;
import com.palacemc.core.command.CoreCommand;
import com.palacemc.core.player.CPlayer;
import com.palacemc.core.player.Rank;
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

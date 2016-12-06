package com.palacemc.essentials.commands;

import com.palacemc.essentials.EssentialsMain;
import com.thepalace.core.Core;
import com.thepalace.core.command.CommandException;
import com.thepalace.core.command.CommandMeta;
import com.thepalace.core.command.CoreCommand;
import com.thepalace.core.config.LanguageFormatter;
import com.thepalace.core.player.CPlayer;
import com.google.common.base.Joiner;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.stream.Collectors;

@CommandMeta(aliases = {"who"}, description = "Lists all players on the server.")
public class ListCommand extends CoreCommand {

    public ListCommand() {
        super("list");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        List<String> playerNames = Core.getPlayerManager().getOnlinePlayers().stream().map(CPlayer::getName).collect(Collectors.toList());
        String playerList = Joiner.on(" ").skipNulls().join(playerNames);
        // Formatter
        LanguageFormatter formatter = EssentialsMain.getPlugin(EssentialsMain.class).getLanguageFormatter();
        String playersOnlineFormat = formatter.getFormat(sender, "command.list.playersOnline").replaceAll("<players-online>", playerList);
        sender.sendMessage(playersOnlineFormat);
    }
}

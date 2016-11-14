package com.palacemc.essentials.commands;

import com.palacemc.core.Core;
import com.palacemc.core.command.CommandException;
import com.palacemc.core.command.CommandMeta;
import com.palacemc.core.command.CoreCommand;
import com.palacemc.core.player.CPlayer;
import com.google.common.base.Joiner;
import org.bukkit.ChatColor;
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
        sender.sendMessage(ChatColor.GOLD + "Players online: " + ChatColor.GREEN + playerList);
    }
}

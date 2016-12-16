package com.palacemc.core.commands;

import com.palacemc.core.command.CommandException;
import com.palacemc.core.command.CommandMeta;
import com.palacemc.core.command.CoreCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Created by Marc on 12/11/16.
 */
@CommandMeta(aliases = {"rl"}, description = "Safely stop the server.")
public class ReloadCommand extends CoreCommand {

    public ReloadCommand() {
        super("reload");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        sender.sendMessage(ChatColor.RED + "Disabled");
    }
}

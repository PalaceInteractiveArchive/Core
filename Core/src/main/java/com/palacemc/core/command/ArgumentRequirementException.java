package com.palacemc.core.command;

import org.bukkit.ChatColor;

public final class ArgumentRequirementException extends CommandException implements FriendlyException {

    public ArgumentRequirementException(String message) {
        super(message);
    }

    @Override
    public String getFriendlyMessage(CoreCommand command) {
        return ChatColor.RED + this.getMessage();
    }
}

package com.palacemc.core.command;

public final class EmptyHandlerException extends CommandException implements FriendlyException {

    public EmptyHandlerException() {
        super("command.error.invalid.handler");
    }

    @Override
    public String getFriendlyMessage() {
        return getMessage();
    }
}

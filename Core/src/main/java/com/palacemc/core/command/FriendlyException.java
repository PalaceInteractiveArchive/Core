package com.palacemc.core.command;

/**
 * Implement this on any exception that extends {@link CommandException} and the return value of the {@link #getFriendlyMessage(CoreCommand)}
 * method will be displayed instead of a verbose message for the exception in the default handler.
 */
public interface FriendlyException {
    /**
     * Grabs a friendly version of the message to be displayed during an exception.
     * @param command The command that is attempting to get the friendly message.
     * @return A message to be displayed to the user during failure by default.
     */
    String getFriendlyMessage(CoreCommand command);
}

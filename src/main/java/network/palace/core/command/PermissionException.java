package network.palace.core.command;

/**
 * The type Permission exception.
 */
public final class PermissionException extends CommandException implements FriendlyException {

    /**
     * Instantiates a new Permission exception.
     */
    public PermissionException() {
        super("command.error.permissions");
    }

    @Override
    public String getFriendlyMessage() {
        return getMessage();
    }
}

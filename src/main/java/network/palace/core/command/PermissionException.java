package network.palace.core.command;

public final class PermissionException extends CommandException implements FriendlyException {

    public PermissionException() {
        super("command.error.permissions");
    }

    @Override
    public String getFriendlyMessage() {
        return getMessage();
    }
}

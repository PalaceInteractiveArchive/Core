package network.palace.core.command;

public final class ArgumentRequirementException extends CommandException implements FriendlyException {

    public ArgumentRequirementException(String message) {
        super(message);
    }

    @Override
    public String getFriendlyMessage() {
        return getMessage();
    }
}

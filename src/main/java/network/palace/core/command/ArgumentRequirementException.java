package network.palace.core.command;

/**
 * The type Argument requirement exception.
 */
public final class ArgumentRequirementException extends CommandException implements FriendlyException {

    /**
     * Instantiates a new Argument requirement exception.
     *
     * @param message the message
     */
    public ArgumentRequirementException(String message) {
        super(message);
    }

    @Override
    public String getFriendlyMessage() {
        return getMessage();
    }
}

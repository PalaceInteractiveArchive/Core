package network.palace.core.command;

import lombok.Getter;

/**
 * The type Unhandled command exception exception.
 */
public class UnhandledCommandExceptionException extends CommandException {

    @Getter private final Exception causingException;

    /**
     * Instantiates a new Unhandled command exception exception.
     *
     * @param e the e
     */
    public UnhandledCommandExceptionException(Exception e) {
        super("Unhandled exception " + e.getMessage());
        this.causingException = e;
    }
}

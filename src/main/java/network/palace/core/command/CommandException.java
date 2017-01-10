package network.palace.core.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * The type Command exception.
 */
@AllArgsConstructor
public class CommandException extends Exception {
    @Getter private final String message;
}

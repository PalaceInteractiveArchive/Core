package network.palace.core.events;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * The type Core online count update.
 */
@AllArgsConstructor
public class CoreOnlineCountUpdate extends CoreEvent {
    @Getter private int count;
}

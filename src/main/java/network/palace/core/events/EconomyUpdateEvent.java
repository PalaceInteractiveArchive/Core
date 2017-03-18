package network.palace.core.events;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

/**
 * The type Economy update event.
 */
@AllArgsConstructor
public class EconomyUpdateEvent extends CoreEvent {
    @Getter private UUID uuid;
    @Getter private int amount;
    @Getter private boolean isBalance;
}

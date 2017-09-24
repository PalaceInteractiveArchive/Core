package network.palace.core.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import network.palace.core.economy.CurrencyType;

import java.util.UUID;

/**
 * The type Economy update event.
 */
@AllArgsConstructor
public class EconomyUpdateEvent extends CoreEvent {
    @Getter private UUID uuid;
    @Getter private int amount;
    @Getter private CurrencyType currency;
}

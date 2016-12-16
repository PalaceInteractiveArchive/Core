package network.palace.core.events;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

/**
 * Created by Marc on 12/12/16.
 */
@AllArgsConstructor
public class EconomyUpdateEvent extends CoreEvent {
    @Getter private UUID uuid;
    @Getter private int balance;
    @Getter private boolean isBalance;
}

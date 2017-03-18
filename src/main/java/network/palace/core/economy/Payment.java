package network.palace.core.economy;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

/**
 * The type Payment.
 */
@AllArgsConstructor
public class Payment {
    @Getter private UUID uuid;
    @Getter private int amount;
    @Getter private String source;
}

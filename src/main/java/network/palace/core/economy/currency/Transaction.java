package network.palace.core.economy.currency;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import network.palace.core.economy.TransactionCallback;

import java.util.UUID;

@RequiredArgsConstructor
public class Transaction {
    @Getter private final UUID paymentId = UUID.randomUUID();
    @Getter private final UUID playerId;
    @Getter private final CurrencyType type;
    @Getter private final int amount;
    @Getter private final String source;
    @Getter private final TransactionCallback callback;
}

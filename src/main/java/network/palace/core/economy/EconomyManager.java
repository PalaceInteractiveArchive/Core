package network.palace.core.economy;

import network.palace.core.Core;
import network.palace.core.economy.currency.CurrencyType;
import network.palace.core.economy.currency.Transaction;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

public class EconomyManager {
    private final Map<UUID, Transaction> transactions = new HashMap<>();

    /**
     * Instantiates a new Economy manager.
     */
    public EconomyManager() {
        Core.runTaskTimer(() -> {
            boolean started = false;
            try {
                // Process at most 20 transactions per second
                Map<UUID, Transaction> localMap = getPartOfMap(transactions, 20);
                started = true;
                // Remove payments about to be processed from main transactions map
                localMap.keySet().forEach(transactions::remove);
                // Asynchronously handle database calls for localMap payments
                Core.runTaskAsynchronously(() -> {
                    for (Map.Entry<UUID, Transaction> entry : new HashSet<>(localMap.entrySet())) {
                        Transaction transaction = entry.getValue();
                        if (transaction.getAmount() == 0) {
                            if (transaction.getCallback() != null)
                                transaction.getCallback().handled(false, "Cannot process transaction of amount 0.");
                            continue;
                        }
                        try {
                            Core.getMongoHandler().changeAmount(transaction.getPlayerId(), transaction.getAmount(), transaction.getSource(), transaction.getType(), false);
                            if (transaction.getCallback() != null) transaction.getCallback().handled(true, "");
                        } catch (Exception e) {
                            e.printStackTrace();
                            if (transaction.getCallback() != null)
                                transaction.getCallback().handled(false, "An error occurred while contacting the database to process this transaction.");
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                if (started) {
                    transactions.values().forEach(transaction -> {
                        if (transaction.getCallback() != null)
                            transaction.getCallback().handled(false, "An error occurred while processing this transaction.");
                    });
                    transactions.clear();
                }
            }
        }, 0L, 20L);
    }

    private Map<UUID, Transaction> getPartOfMap(Map<UUID, Transaction> map, int amount) {
        if (map.size() <= amount) return new HashMap<>(map);
        Map<UUID, Transaction> mapPart = new HashMap<>();
        int i = 0;
        for (Map.Entry<UUID, Transaction> entry : map.entrySet()) {
            if (i >= amount) break;
            mapPart.put(entry.getKey(), entry.getValue());
            i++;
        }
        return mapPart;
    }

    /**
     * Add a transaction to the queue
     *
     * @param uuid     the uuid of the target of the transaction
     * @param amount   the amount of the transaction
     * @param source   the source of the transaction
     * @param callback the callback to be executed after the transaction completes
     * @implNote Do NOT call this method directly, use methods in {{@link network.palace.core.player.CPlayer}}
     */
    public void addTransaction(UUID uuid, int amount, String source, CurrencyType type, TransactionCallback callback) {
        Transaction transaction = new Transaction(uuid, type, amount, source, callback);
        transactions.put(transaction.getPaymentId(), transaction);
    }
}

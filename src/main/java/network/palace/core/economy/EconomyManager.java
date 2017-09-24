package network.palace.core.economy;

import network.palace.core.Core;
import network.palace.core.events.EconomyUpdateEvent;
import network.palace.core.mongo.MongoHandler;

import java.util.UUID;

/**
 * The type Economy manager.
 */
public class EconomyManager {

    public void changeAmount(UUID uuid, int amount, String source, CurrencyType type, boolean set) {
        MongoHandler handler = Core.getMongoHandler();
        handler.changeAmount(uuid, amount, type, set);
        handler.logTransaction(uuid, amount, source, type, set);
        new EconomyUpdateEvent(uuid, handler.getCurrency(uuid, type), type).call();
    }
}

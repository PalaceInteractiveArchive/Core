package network.palace.core.dashboard.packets;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * The type Base packet.
 */
@AllArgsConstructor
public class BasePacket {

    @Getter protected int id = 0;

    public BasePacket fromJSON(JsonObject obj) {
        return this;
    }

    public JsonObject getJSON() {
        return null;
    }
}
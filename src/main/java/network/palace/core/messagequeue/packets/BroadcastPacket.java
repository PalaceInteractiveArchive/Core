package network.palace.core.messagequeue.packets;

import com.google.gson.JsonObject;
import lombok.Getter;

public class BroadcastPacket extends MQPacket {
    @Getter private final String sender, message;

    public BroadcastPacket(JsonObject object) {
        super(PacketID.Global.BROADCAST.getId(), object);
        this.sender = object.get("sender").getAsString();
        this.message = object.get("message").getAsString();
    }

    public BroadcastPacket(String sender, String message) {
        super(PacketID.Global.BROADCAST.getId(), null);
        this.sender = sender;
        this.message = message;
    }

    @Override
    public JsonObject getJSON() {
        JsonObject object = getBaseJSON();
        object.addProperty("sender", sender);
        object.addProperty("message", message);
        return object;
    }
}

package network.palace.core.messagequeue.packets;

import com.google.gson.JsonObject;
import lombok.Getter;

public class BroadcastComponentPacket extends MQPacket {
    @Getter private final String sender, serializedMessage;

    public BroadcastComponentPacket(JsonObject object) {
        super(PacketID.Global.BROADCAST_COMPONENT.getId(), object);
        this.sender = object.get("sender").getAsString();
        this.serializedMessage = object.get("serializedMessage").getAsString();
    }

    public BroadcastComponentPacket(String sender, String serializedMessage) {
        super(PacketID.Global.BROADCAST_COMPONENT.getId(), null);
        this.sender = sender;
        this.serializedMessage = serializedMessage;
    }

    @Override
    public JsonObject getJSON() {
        JsonObject object = getBaseJSON();
        object.addProperty("sender", sender);
        object.addProperty("serializedMessage", serializedMessage);
        return object;
    }
}

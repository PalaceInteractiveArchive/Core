package network.palace.core.messagequeue.packets;

import com.google.gson.JsonObject;
import lombok.Getter;

import java.util.UUID;

public class MentionPacket extends MQPacket {
    @Getter private UUID uuid;

    public MentionPacket(JsonObject object) {
        super(PacketID.Global.MENTION.getId(), object);
        this.uuid = UUID.fromString(object.get("uuid").getAsString());
    }

    public MentionPacket(UUID uuid) {
        super(PacketID.Global.MENTION.getId(), null);
        this.uuid = uuid;
    }

    @Override
    public JsonObject getJSON() {
        JsonObject object = getBaseJSON();
        object.addProperty("uuid", uuid.toString());
        return object;
    }
}

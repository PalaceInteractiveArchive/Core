package network.palace.core.messagequeue.packets;

import com.google.gson.JsonObject;
import lombok.Getter;

import java.util.UUID;

@Getter
public class KickPlayerPacket extends MQPacket {
    private final UUID uuid;
    private final String reason;
    private final boolean componentMessage;

    public KickPlayerPacket(JsonObject object) {
        super(PacketID.Global.KICK_PLAYER.getId(), object);
        this.uuid = UUID.fromString(object.get("uuid").getAsString());
        this.reason = object.get("reason").getAsString();
        this.componentMessage = object.get("componentMessage").getAsBoolean();
    }

    public KickPlayerPacket(UUID uuid, String reason, boolean componentMessage) {
        super(PacketID.Global.KICK_PLAYER.getId(), null);
        this.uuid = uuid;
        this.reason = reason;
        this.componentMessage = componentMessage;
    }

    @Override
    public JsonObject getJSON() {
        JsonObject object = getBaseJSON();
        object.addProperty("uuid", uuid.toString());
        object.addProperty("reason", reason);
        object.addProperty("componentMessage", componentMessage);
        return object;
    }
}
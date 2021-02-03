package network.palace.core.messagequeue.packets;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@RequiredArgsConstructor
public abstract class MQPacket {
    @Getter private int id;
    @Getter protected UUID sendingProxy = null;

    protected MQPacket(int id, JsonObject object) {
        this.id = id;
        if (object != null && object.get("id").getAsInt() != id)
            throw new IllegalArgumentException("Packet id does not match!");
    }

    public MQPacket(JsonObject obj) {
    }

    public abstract JsonObject getJSON();

    protected JsonObject getBaseJSON() {
        JsonObject object = new JsonObject();
        object.addProperty("id", id);
        if (sendingProxy != null) object.addProperty("proxyID", sendingProxy.toString());
        return object;
    }

    public byte[] toBytes() {
        JsonObject obj = getJSON();
        if (obj != null) return obj.toString().getBytes(StandardCharsets.UTF_8);
        else return new byte[0];
    }
}

package network.palace.core.messagequeue.packets;

import com.google.gson.JsonObject;
import lombok.Getter;

@Getter
public class EmptyServerPacket extends MQPacket {
    private final String server;

    public EmptyServerPacket(JsonObject object) {
        super(PacketID.Global.EMPTY_SERVER.getId(), object);
        this.server = object.get("server").getAsString();
    }

    public EmptyServerPacket(String server) {
        super(PacketID.Global.EMPTY_SERVER.getId(), null);
        this.server = server;
    }

    @Override
    public JsonObject getJSON() {
        JsonObject object = getBaseJSON();
        object.addProperty("server", server);
        return object;
    }
}
package network.palace.core.dashboard.packets.audio;

import com.google.gson.JsonObject;
import lombok.Getter;
import network.palace.core.dashboard.packets.BasePacket;
import network.palace.core.dashboard.packets.PacketID;

/**
 * The type Packet client accept.
 */
public class PacketClientAccept extends BasePacket {

    @Getter private String serverName = "";

    /**
     * Instantiates a new Packet client accept.
     *
     * @param serverName the name of the server
     */
    public PacketClientAccept(String serverName) {
        super(PacketID.CLIENT_ACCEPTED.getID());
        this.serverName = serverName;
    }

    public PacketClientAccept fromJSON(JsonObject obj) {
        try {
            this.serverName = obj.get("serverName").getAsString();
        } catch (Exception e) {
            return null;
        }
        return this;
    }

    public JsonObject getJSON() {
        JsonObject obj = new JsonObject();
        try {
            obj.addProperty("id", this.id);
            obj.addProperty("serverName", this.serverName);
        } catch (Exception e) {
            return null;
        }
        return obj;
    }
}
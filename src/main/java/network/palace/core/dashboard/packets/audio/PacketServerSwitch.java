package network.palace.core.dashboard.packets.audio;

import com.google.gson.JsonObject;
import lombok.Getter;
import network.palace.core.dashboard.packets.BasePacket;
import network.palace.core.dashboard.packets.PacketID;

/**
 * The type Packet server switch.
 */
public class PacketServerSwitch extends BasePacket {

    @Getter private String server = "";

    /**
     * Instantiates a new Packet server switch.
     *
     * @param server the server
     */
    public PacketServerSwitch(String server) {
        super(PacketID.SERVER_SWITCH.getID());
        this.server = server;
    }

    public PacketServerSwitch fromJSON(JsonObject obj) {
        try {
            this.server = obj.get("servername").getAsString();
        } catch (Exception e) {
            return null;
        }
        return this;
    }

    public JsonObject getJSON() {
        JsonObject obj = new JsonObject();
        try {
            obj.addProperty("id", this.id);
            obj.addProperty("servername", this.server);
        } catch (Exception e) {
            return null;
        }
        return obj;
    }
}

package network.palace.core.dashboard.packets.audio;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import network.palace.core.dashboard.packets.BasePacket;
import network.palace.core.dashboard.packets.PacketID;

/**
 * The type Packet client accept.
 */
public class PacketClientAccept extends BasePacket {

    @Getter private String servername = "";

    /**
     * Instantiates a new Packet client accept.
     *
     * @param servername the servername
     */
    public PacketClientAccept(String servername) {
        super(PacketID.CLIENT_ACCEPTED.getID());
        this.servername = servername;
    }

    public PacketClientAccept fromJSON(JsonObject obj) {
        try {
            this.servername = obj.get("servername").getAsString();
        } catch (Exception e) {
            return null;
        }
        return this;
    }

    public JsonObject getJSON() {
        JsonObject obj = new JsonObject();
        try {
            obj.addProperty("id", this.id);
            obj.addProperty("servername", this.servername);
        } catch (Exception e) {
            return null;
        }
        return obj;
    }
}
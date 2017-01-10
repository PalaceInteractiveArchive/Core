package network.palace.core.dashboard.packets.dashboard;

import com.google.gson.JsonObject;
import lombok.Getter;
import network.palace.core.dashboard.packets.BasePacket;
import network.palace.core.dashboard.packets.PacketID;

/**
 * The type Packet server name.
 */
public class PacketServerName extends BasePacket {

    @Getter private String name = "";

    /**
     * Instantiates a new Packet server name.
     *
     * @param name the name
     */
    public PacketServerName(String name) {
        super(PacketID.Dashboard.SERVERNAME.getID());
        this.name = name;
    }

    public PacketServerName fromJSON(JsonObject obj) {
        this.name = obj.get("name").getAsString();
        return this;
    }

    public JsonObject getJSON() {
        JsonObject obj = new JsonObject();
        try {
            obj.addProperty("id", this.id);
            obj.addProperty("name", this.name);
        } catch (Exception e) {
            return null;
        }
        return obj;
    }
}

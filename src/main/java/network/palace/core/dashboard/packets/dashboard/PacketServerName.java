package network.palace.core.dashboard.packets.dashboard;

import com.google.gson.JsonObject;
import network.palace.core.dashboard.packets.BasePacket;
import network.palace.core.dashboard.packets.PacketID;

/**
 * Created by Marc on 9/17/16
 */
@SuppressWarnings("unused")
public class PacketServerName extends BasePacket {
    private String name;

    public PacketServerName() {
        this("");
    }

    public PacketServerName(String name) {
        this.id = PacketID.Dashboard.SERVERNAME.getID();
        this.name = name;
    }

    public String getName() {
        return name;
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
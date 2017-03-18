package network.palace.core.dashboard.packets.dashboard;

import com.google.gson.JsonObject;
import lombok.Getter;
import network.palace.core.dashboard.packets.BasePacket;
import network.palace.core.dashboard.packets.PacketID;

/**
 * The type Packet empty server.
 */
public class PacketEmptyServer extends BasePacket {

    @Getter private String server = "";

    /**
     * Instantiates a new Packet empty server.
     *
     * @param server the server
     */
    public PacketEmptyServer(String server) {
        super(PacketID.Dashboard.EMPTYSERVER.getID());
        this.server = server;
    }

    public PacketEmptyServer fromJSON(JsonObject obj) {
        this.server = obj.get("server").getAsString();
        return this;
    }

    public JsonObject getJSON() {
        JsonObject obj = new JsonObject();
        try {
            obj.addProperty("id", this.id);
            obj.addProperty("server", this.server);
        } catch (Exception e) {
            return null;
        }
        return obj;
    }
}

package network.palace.core.dashboard.packets.dashboard;

import com.google.gson.JsonObject;
import network.palace.core.dashboard.packets.BasePacket;
import network.palace.core.dashboard.packets.PacketID;

import java.util.UUID;

/**
 * Created by Marc on 8/22/16
 */
public class PacketSendToServer extends BasePacket {
    private UUID uuid;
    private String server;

    public PacketSendToServer() {
        this(null, "");
    }

    public PacketSendToServer(UUID uuid, String server) {
        super(PacketID.Dashboard.SENDTOSERVER.getID());
        this.uuid = uuid;
        this.server = server;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public String getServer() {
        return server;
    }

    public PacketSendToServer fromJSON(JsonObject obj) {
        try {
            this.uuid = UUID.fromString(obj.get("uuid").getAsString());
        } catch (Exception e) {
            this.uuid = null;
        }
        this.server = obj.get("server").getAsString();
        return this;
    }

    public JsonObject getJSON() {
        JsonObject obj = new JsonObject();
        try {
            obj.addProperty("id", this.id);
            obj.addProperty("uuid", this.uuid.toString());
            obj.addProperty("server", this.server);
        } catch (Exception e) {
            return null;
        }
        return obj;
    }
}

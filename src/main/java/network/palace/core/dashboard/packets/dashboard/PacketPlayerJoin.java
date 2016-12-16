package network.palace.core.dashboard.packets.dashboard;

import com.google.gson.JsonObject;
import network.palace.core.dashboard.packets.BasePacket;
import network.palace.core.dashboard.packets.PacketID;

import java.util.UUID;

/**
 * Created by Marc on 7/14/16
 */
@SuppressWarnings("unused")
public class PacketPlayerJoin extends BasePacket {
    private UUID uuid;
    private String username;
    private String server;
    private String address;

    public PacketPlayerJoin() {
        this(null, "", "", "");
    }

    public PacketPlayerJoin(UUID uuid, String username, String server, String address) {
        this.id = PacketID.Dashboard.PLAYERJOIN.getID();
        this.uuid = uuid;
        this.username = username;
        this.server = server;
        this.address = address;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public String getUsername() {
        return username;
    }

    public String getServer() {
        return server;
    }

    public String getAddress() {
        return address;
    }

    public PacketPlayerJoin fromJSON(JsonObject obj) {
        try {
            this.uuid = UUID.fromString(obj.get("uuid").getAsString());
        } catch (Exception e) {
            this.uuid = null;
        }
        this.username = obj.get("username").getAsString();
        this.server = obj.get("server").getAsString();
        this.address = obj.get("address").getAsString();
        return this;
    }

    public JsonObject getJSON() {
        JsonObject obj = new JsonObject();
        try {
            obj.addProperty("id", this.id);
            obj.addProperty("uuid", this.uuid.toString());
            obj.addProperty("username", this.username);
            obj.addProperty("server", this.server);
            obj.addProperty("address", this.address);
        } catch (Exception e) {
            return null;
        }
        return obj;
    }

    @SuppressWarnings("SameReturnValue")
    public String getRank() {
        return null;
    }
}

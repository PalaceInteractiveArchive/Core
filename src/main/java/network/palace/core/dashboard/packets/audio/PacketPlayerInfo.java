package network.palace.core.dashboard.packets.audio;

import com.google.gson.JsonObject;
import network.palace.core.dashboard.packets.BasePacket;
import network.palace.core.dashboard.packets.PacketID;

import java.util.UUID;

/**
 * Created by Marc on 6/15/15
 */
@SuppressWarnings("unused")
public class PacketPlayerInfo extends BasePacket {
    private UUID uuid;
    private String username;
    private int auth;
    private String server;

    public PacketPlayerInfo() {
        this(null, "", 0, "");
    }

    public PacketPlayerInfo(UUID uuid, String username, int auth, String server) {
        this.id = PacketID.PLAYERINFO.getID();
        this.uuid = uuid;
        this.username = username;
        this.auth = auth;
        this.server = server;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public String getUsername() {
        return username;
    }

    public int getAuth() {
        return auth;
    }

    public String getServer() {
        return server;
    }

    public PacketPlayerInfo fromJSON(JsonObject obj) {
        try {
            this.uuid = UUID.fromString(obj.get("uuid").getAsString());
        } catch (Exception e) {
            this.uuid = null;
        }
        this.username = obj.get("username").getAsString();
        this.auth = obj.get("auth").getAsInt();
        this.server = obj.get("server").getAsString();
        return this;
    }

    public JsonObject getJSON() {
        JsonObject obj = new JsonObject();
        try {
            obj.addProperty("id", this.id);
            obj.addProperty("uuid", uuid != null ? uuid.toString() : null);
            obj.addProperty("username", this.username);
            obj.addProperty("auth", this.auth);
            obj.addProperty("server", this.server);
        } catch (Exception e) {
            return null;
        }
        return obj;
    }
}
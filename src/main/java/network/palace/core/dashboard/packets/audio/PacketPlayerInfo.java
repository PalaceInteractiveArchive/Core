package network.palace.core.dashboard.packets.audio;

import com.google.gson.JsonObject;
import lombok.Getter;
import network.palace.core.dashboard.packets.BasePacket;
import network.palace.core.dashboard.packets.PacketID;

import java.util.UUID;

/**
 * The type Packet player info.
 */
public class PacketPlayerInfo extends BasePacket {

    @Getter private UUID uniqueId = null;
    @Getter private String username = "";
    @Getter private int auth = 0;
    @Getter private String server = "";

    /**
     * Instantiates a new Packet player info.
     */
    public PacketPlayerInfo() {
        this(null, "", 0, "");
    }

    /**
     * Instantiates a new Packet player info.
     *
     * @param uniqueId the unique id
     * @param username the username
     * @param auth     the auth
     * @param server   the server
     */
    public PacketPlayerInfo(UUID uniqueId, String username, int auth, String server) {
        super(PacketID.PLAYERINFO.getID());
        this.uniqueId = uniqueId;
        this.username = username;
        this.auth = auth;
        this.server = server;
    }

    public PacketPlayerInfo fromJSON(JsonObject obj) {
        try {
            this.uniqueId = UUID.fromString(obj.get("uuid").getAsString());
        } catch (Exception e) {
            this.uniqueId = null;
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
            obj.addProperty("uuid", uniqueId != null ? uniqueId.toString() : null);
            obj.addProperty("username", this.username);
            obj.addProperty("auth", this.auth);
            obj.addProperty("server", this.server);
        } catch (Exception e) {
            return null;
        }
        return obj;
    }
}

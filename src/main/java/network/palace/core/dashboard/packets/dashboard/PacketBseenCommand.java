package network.palace.core.dashboard.packets.dashboard;

import com.google.gson.JsonObject;
import network.palace.core.dashboard.packets.BasePacket;
import network.palace.core.dashboard.packets.PacketID;

import java.util.UUID;

/**
 * Created by Marc on 8/24/16
 */
@SuppressWarnings("unused")
public class PacketBseenCommand extends BasePacket {
    private UUID uuid;
    private String username;
    private String address;
    private String server;
    private boolean online;

    public PacketBseenCommand() {
        this(null, "", "", "", false);
    }

    public PacketBseenCommand(UUID uuid, String username, String address, String server, boolean online) {
        this.id = PacketID.Dashboard.BSEENCOMMAND.getID();
        this.uuid = uuid;
        this.username = username;
        this.address = address;
        this.server = server;
        this.online = online;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public String getUsername() {
        return username;
    }

    public String getAddress() {
        return address;
    }

    public String getServer() {
        return server;
    }

    public boolean isOnline() {
        return online;
    }

    public PacketBseenCommand fromJSON(JsonObject obj) {
        try {
            this.uuid = UUID.fromString(obj.get("uuid").getAsString());
        } catch (Exception e) {
            this.uuid = null;
        }
        this.username = obj.get("username").getAsString();
        this.address = obj.get("address").getAsString();
        this.server = obj.get("server").getAsString();
        this.online = obj.get("online").getAsBoolean();
        return this;
    }

    public JsonObject getJSON() {
        JsonObject obj = new JsonObject();
        try {
            obj.addProperty("id", this.id);
            obj.addProperty("uuid", this.uuid.toString());
            obj.addProperty("username", this.username);
            obj.addProperty("address", this.address);
            obj.addProperty("server", this.server);
            obj.addProperty("online", this.online);
        } catch (Exception e) {
            return null;
        }
        return obj;
    }
}
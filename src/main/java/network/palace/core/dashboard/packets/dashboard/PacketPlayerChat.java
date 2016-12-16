package network.palace.core.dashboard.packets.dashboard;

import com.google.gson.JsonObject;
import network.palace.core.dashboard.packets.BasePacket;
import network.palace.core.dashboard.packets.PacketID;

import java.util.UUID;

/**
 * Created by Marc on 7/15/16
 */
@SuppressWarnings("unused")
public class PacketPlayerChat extends BasePacket {
    private UUID uuid;
    private String message;

    public PacketPlayerChat() {
        this(null, "");
    }

    public PacketPlayerChat(UUID uuid, String message) {
        this.id = PacketID.Dashboard.PLAYERCHAT.getID();
        this.uuid = uuid;
        this.message = message;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public String getMessage() {
        return message;
    }

    public PacketPlayerChat fromJSON(JsonObject obj) {
        try {
            this.uuid = UUID.fromString(obj.get("uuid").getAsString());
        } catch (Exception e) {
            this.uuid = null;
        }
        this.message = obj.get("message").getAsString();
        return this;
    }

    public JsonObject getJSON() {
        JsonObject obj = new JsonObject();
        try {
            obj.addProperty("id", this.id);
            obj.addProperty("uuid", this.uuid.toString());
            obj.addProperty("message", this.message);
        } catch (Exception e) {
            return null;
        }
        return obj;
    }
}
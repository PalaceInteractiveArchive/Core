package network.palace.core.dashboard.packets.audio;

import com.google.gson.JsonObject;
import lombok.Getter;
import network.palace.core.dashboard.packets.BasePacket;
import network.palace.core.dashboard.packets.PacketID;

/**
 * The type Packet kick.
 */
public class PacketKick extends BasePacket {

    @Getter private String message = "";
    @Getter private String reason = "";

    /**
     * Instantiates a new Packet kick.
     */
    public PacketKick() {
        this("", "");
    }

    /**
     * Instantiates a new Packet kick.
     *
     * @param message the message
     */
    public PacketKick(String message) {
        this(message, "DEFAULT");
    }

    /**
     * Instantiates a new Packet kick.
     *
     * @param message the message
     * @param reason  the reason
     */
    public PacketKick(String message, String reason) {
        super(PacketID.KICK.getID());
        this.message = message;
        this.reason = reason;
    }

    public PacketKick fromJSON(JsonObject obj) {
        try {
            this.reason = obj.get("reason").getAsString();
            this.message = obj.get("message").getAsString();
        } catch (Exception e) {
            return null;
        }
        return this;
    }

    public JsonObject getJSON() {
        JsonObject obj = new JsonObject();
        try {
            obj.addProperty("id", this.id);
            obj.addProperty("message", this.message);
            obj.addProperty("reason", this.reason);
        } catch (Exception e) {
            return null;
        }
        return obj;
    }
}

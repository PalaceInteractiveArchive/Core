package network.palace.core.dashboard.packets.dashboard;

import com.google.gson.JsonObject;
import lombok.Getter;
import network.palace.core.dashboard.packets.BasePacket;
import network.palace.core.dashboard.packets.PacketID;

import java.util.UUID;

/**
 * The type Packet mention.
 */
public class PacketMention extends BasePacket {

    @Getter private UUID uuid = null;

    /**
     * Instantiates a new Packet mention.
     */
    public PacketMention() {
        this(null);
    }

    /**
     * Instantiates a new Packet mention.
     *
     * @param uuid the uuid
     */
    public PacketMention(UUID uuid) {
        super(PacketID.Dashboard.MENTION.getID());
        this.uuid = uuid;
    }

    /**
     * Gets unique id.
     *
     * @return the unique id
     */
    public UUID getUniqueId() {
        return uuid;
    }

    public PacketMention fromJSON(JsonObject obj) {
        try {
            this.uuid = UUID.fromString(obj.get("uuid").getAsString());
        } catch (Exception e) {
            this.uuid = null;
        }
        return this;
    }

    public JsonObject getJSON() {
        JsonObject obj = new JsonObject();
        try {
            obj.addProperty("id", this.id);
            obj.addProperty("uuid", this.uuid.toString());
        } catch (Exception e) {
            return null;
        }
        return obj;
    }
}

package network.palace.core.dashboard.packets.audio;

import com.google.gson.JsonObject;
import lombok.Getter;
import network.palace.core.dashboard.packets.BasePacket;
import network.palace.core.dashboard.packets.PacketID;

import java.util.UUID;

/**
 * The type Packet container.
 */
public class PacketContainer extends BasePacket {

    @Getter private UUID uuid = null;
    @Getter private String container = "";

    /**
     * Instantiates a new Packet container.
     *
     * @param uuid      the uuid
     * @param container the container
     */
    public PacketContainer(UUID uuid, String container) {
        super(PacketID.CONTAINER.getID());
        this.uuid = uuid;
        this.container = container;
    }

    public PacketContainer fromJSON(JsonObject obj) {
        try {
            this.uuid = UUID.fromString(obj.get("uuid").getAsString());
        } catch (Exception e) {
            this.uuid = null;
        }
        this.container = obj.get("container").getAsString();
        return this;
    }

    public JsonObject getJSON() {
        JsonObject obj = new JsonObject();
        try {
            obj.addProperty("id", this.id);
            obj.addProperty("uuid", uuid != null ? uuid.toString() : null);
            obj.addProperty("container", this.container);
        } catch (Exception e) {
            return null;
        }
        return obj;
    }
}

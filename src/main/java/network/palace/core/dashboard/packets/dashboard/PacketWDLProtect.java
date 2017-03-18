package network.palace.core.dashboard.packets.dashboard;

import com.google.gson.JsonObject;
import lombok.Getter;
import network.palace.core.dashboard.packets.BasePacket;
import network.palace.core.dashboard.packets.PacketID;

import java.util.UUID;

/**
 * The type Packet wdl protect.
 */
public class PacketWDLProtect extends BasePacket {

    @Getter private UUID uuid = null;

    /**
     * Instantiates a new Packet wdl protect.
     *
     * @param uuid the uuid
     */
    public PacketWDLProtect(UUID uuid) {
        super(PacketID.Dashboard.WDLPROTECT.getID());
        this.uuid = uuid;
    }

    public PacketWDLProtect fromJSON(JsonObject obj) {
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

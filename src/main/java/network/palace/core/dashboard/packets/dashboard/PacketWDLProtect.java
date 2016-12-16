package network.palace.core.dashboard.packets.dashboard;

import com.google.gson.JsonObject;
import network.palace.core.dashboard.packets.BasePacket;
import network.palace.core.dashboard.packets.PacketID;

import java.util.UUID;

/**
 * Created by Marc on 9/17/16
 */
@SuppressWarnings("unused")
public class PacketWDLProtect extends BasePacket {
    private UUID uuid;

    public PacketWDLProtect() {
        this(null);
    }

    public PacketWDLProtect(UUID uuid) {
        this.id = PacketID.Dashboard.WDLPROTECT.getID();
        this.uuid = uuid;
    }

    public UUID getUniqueId() {
        return uuid;
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
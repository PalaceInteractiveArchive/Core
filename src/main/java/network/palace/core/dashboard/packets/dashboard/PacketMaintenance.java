package network.palace.core.dashboard.packets.dashboard;

import com.google.gson.JsonObject;
import network.palace.core.dashboard.packets.BasePacket;
import network.palace.core.dashboard.packets.PacketID;

/**
 * Created by Marc on 9/12/16
 */
@SuppressWarnings("unused")
public class PacketMaintenance extends BasePacket {
    private boolean maintenance;

    public PacketMaintenance() {
        this(false);
    }

    public PacketMaintenance(boolean maintenance) {
        this.id = PacketID.Dashboard.MAINTENANCE.getID();
        this.maintenance = maintenance;
    }

    public boolean isMaintenance() {
        return maintenance;
    }

    public PacketMaintenance fromJSON(JsonObject obj) {
        this.maintenance = obj.get("maintenance").getAsBoolean();
        return this;
    }

    public JsonObject getJSON() {
        JsonObject obj = new JsonObject();
        try {
            obj.addProperty("id", this.id);
            obj.addProperty("maintenance", this.maintenance);
        } catch (Exception e) {
            return null;
        }
        return obj;
    }
}
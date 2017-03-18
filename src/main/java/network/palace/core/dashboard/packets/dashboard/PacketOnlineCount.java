package network.palace.core.dashboard.packets.dashboard;

import com.google.gson.JsonObject;
import lombok.Getter;
import network.palace.core.dashboard.packets.BasePacket;
import network.palace.core.dashboard.packets.PacketID;

/**
 * The type Packet online count.
 */
public class PacketOnlineCount extends BasePacket {

    @Getter private int count = 0;

    /**
     * Instantiates a new Packet online count.
     */
    public PacketOnlineCount() {
        this(0);
    }

    /**
     * Instantiates a new Packet online count.
     *
     * @param count the count
     */
    public PacketOnlineCount(int count) {
        super(PacketID.Dashboard.ONLINECOUNT.getID());
        this.count = count;
    }

    public PacketOnlineCount fromJSON(JsonObject obj) {
        this.count = obj.get("count").getAsInt();
        return this;
    }

    public JsonObject getJSON() {
        JsonObject obj = new JsonObject();
        try {
            obj.addProperty("id", this.id);
            obj.addProperty("count", this.count);
        } catch (Exception e) {
            return null;
        }
        return obj;
    }
}

package network.palace.core.dashboard.packets.audio;

import com.google.gson.JsonObject;
import lombok.Getter;
import network.palace.core.dashboard.packets.BasePacket;
import network.palace.core.dashboard.packets.PacketID;

/**
 * The type Packet area stop.
 */
public class PacketAreaStop extends BasePacket {

    @Getter private int audioID = 0;
    @Getter private int fadetime = 0;

    /**
     * Instantiates a new Packet area stop.
     */
    public PacketAreaStop() {
        this(-1, 0);
    }

    /**
     * Instantiates a new Packet area stop.
     *
     * @param audioid  the audioid
     * @param fadetime the fadetime
     */
    public PacketAreaStop(int audioid, int fadetime) {
        super(PacketID.AREA_STOP.getID());
        this.audioID = audioid;
        this.fadetime = fadetime;
    }

    public PacketAreaStop fromJSON(JsonObject obj) {
        try {
            this.audioID = obj.get("audioid").getAsInt();
            this.fadetime = obj.get("fadetime").getAsInt();
        } catch (Exception e) {
            return null;
        }
        return this;
    }

    public JsonObject getJSON() {
        JsonObject obj = new JsonObject();
        try {
            obj.addProperty("id", this.id);
            obj.addProperty("audioid", this.audioID);
            obj.addProperty("fadetime", this.fadetime);
        } catch (Exception e) {
            return null;
        }
        return obj;
    }
}

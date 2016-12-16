package network.palace.core.dashboard.packets.audio;

import com.google.gson.JsonObject;
import network.palace.core.dashboard.packets.BasePacket;
import network.palace.core.dashboard.packets.PacketID;

/**
 * Created by Marc on 6/15/15
 */
@SuppressWarnings("unused")
public class PacketAreaStop extends BasePacket {
    private int audioid = 0;
    private int fadetime = 0;

    public PacketAreaStop() {
        this(-1, 0);
    }

    public PacketAreaStop(int audioid, int fadetime) {
        this.id = PacketID.AREA_STOP.getID();

        this.audioid = audioid;
        this.fadetime = fadetime;
    }

    public int getAudioID() {
        return this.audioid;
    }

    public PacketAreaStop fromJSON(JsonObject obj) {
        try {
            this.audioid = obj.get("audioid").getAsInt();
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
            obj.addProperty("audioid", this.audioid);
            obj.addProperty("fadetime", this.fadetime);
        } catch (Exception e) {
            return null;
        }
        return obj;
    }
}
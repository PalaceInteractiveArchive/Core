package network.palace.core.dashboard.packets.audio;

import com.google.gson.JsonObject;
import lombok.Getter;
import network.palace.core.dashboard.packets.BasePacket;
import network.palace.core.dashboard.packets.PacketID;

/**
 * The type Packet area start.
 */
public class PacketAreaStart extends BasePacket {

    @Getter private int audioID = 0;
    @Getter private String name = "";
    @Getter private float volume = 1.0F;
    @Getter private int fadetime = 0;
    @Getter private boolean repeat = true;

    /**
     * Instantiates a new Packet area start.
     */
    public PacketAreaStart() {
        this(-1, "", 1.0F, 0, true);
    }

    /**
     * Instantiates a new Packet area start.
     *
     * @param audioID  the audio id
     * @param name     the name
     * @param volume   the volume
     * @param fadetime the fadetime
     * @param repeat   the repeat
     */
    public PacketAreaStart(int audioID, String name, float volume, int fadetime, boolean repeat) {
        super(PacketID.AREA_START.getID());
        this.audioID = audioID;
        this.name = name;
        this.volume = volume;
        this.fadetime = fadetime;
        this.repeat = repeat;
    }

    public PacketAreaStart fromJSON(JsonObject obj) {
        try {
            this.audioID = obj.get("audioid").getAsInt();
            this.name = obj.get("name").getAsString();
            this.volume = obj.get("volume").getAsFloat();
            this.fadetime = obj.get("fadetime").getAsInt();
            this.repeat = obj.get("repeat").getAsBoolean();
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
            obj.addProperty("name", this.name);
            obj.addProperty("volume", this.volume);
            obj.addProperty("fadetime", this.fadetime);
            obj.addProperty("repeat", this.repeat);
        } catch (Exception e) {
            return null;
        }
        return obj;
    }
}

package network.palace.core.dashboard.packets.audio;

import com.google.gson.JsonObject;
import lombok.Getter;
import network.palace.core.dashboard.packets.BasePacket;
import network.palace.core.dashboard.packets.PacketID;

/**
 * The type Packet play once global.
 */
public class PacketPlayOnceGlobal extends BasePacket {

    @Getter private int audioID = 0;
    @Getter private String name = "";
    @Getter private float volume = 1.0F;

    /**
     * Instantiates a new Packet play once global.
     */
    public PacketPlayOnceGlobal() {
        this(-1, "", 1.0F);
    }

    /**
     * Instantiates a new Packet play once global.
     *
     * @param audioid the audioid
     * @param name    the name
     */
    public PacketPlayOnceGlobal(int audioid, String name) {
        this(audioid, name, 1.0F);
    }

    /**
     * Instantiates a new Packet play once global.
     *
     * @param audioID the audio id
     * @param name    the name
     * @param volume  the volume
     */
    public PacketPlayOnceGlobal(int audioID, String name, float volume) {
        super(PacketID.GLOBAL_PLAY_ONCE.getID());
        this.audioID = audioID;
        this.name = name;
        this.volume = volume;
    }

    public PacketPlayOnceGlobal fromJSON(JsonObject obj) {
        try {
            this.audioID = obj.get("audioid").getAsInt();
            this.name = obj.get("name").getAsString();
            this.volume = obj.get("volume").getAsFloat();
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
        } catch (Exception e) {
            return null;
        }
        return obj;
    }
}

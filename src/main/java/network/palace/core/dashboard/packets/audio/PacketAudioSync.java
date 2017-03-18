package network.palace.core.dashboard.packets.audio;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import network.palace.core.dashboard.packets.BasePacket;
import network.palace.core.dashboard.packets.PacketID;

/**
 * The type Packet audio sync.
 */
public class PacketAudioSync extends BasePacket {

    @Getter private int audioID = 0;
    @Getter private double seconds = 1.0D;
    @Getter @Setter private double margin = 0.0D;

    /**
     * Instantiates a new Packet audio sync.
     */
    public PacketAudioSync() {
        this(-1, 0.0F);
    }

    /**
     * Instantiates a new Packet audio sync.
     *
     * @param audioID the audio id
     * @param volume  the volume
     */
    public PacketAudioSync(int audioID, float volume) {
        super(PacketID.AUDIO_SYNC.getID());
        this.audioID = audioID;
        this.seconds = volume;
    }

    /**
     * Instantiates a new Packet audio sync.
     *
     * @param audioID the audio id
     * @param volume  the volume
     * @param margin  the margin
     */
    public PacketAudioSync(int audioID, float volume, double margin) {
        this(audioID, volume);
        this.margin = margin;
    }

    public PacketAudioSync fromJSON(JsonObject obj) {
        try {
            this.audioID = obj.get("audioid").getAsInt();
            this.seconds = obj.get("volume").getAsDouble();
            this.margin = obj.get("margin").getAsDouble();
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
            obj.addProperty("seconds", this.seconds);
            obj.addProperty("margin", this.margin);
        } catch (Exception e) {
            return null;
        }
        return obj;
    }
}

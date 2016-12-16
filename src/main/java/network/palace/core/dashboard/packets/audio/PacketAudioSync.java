package network.palace.core.dashboard.packets.audio;

import com.google.gson.JsonObject;
import network.palace.core.dashboard.packets.BasePacket;
import network.palace.core.dashboard.packets.PacketID;

/**
 * Created by Marc on 6/15/15
 */
@SuppressWarnings("unused")
public class PacketAudioSync extends BasePacket {
    private int audioid = 0;
    private double seconds = 1.0D;
    private double margin = 0.0D;

    public PacketAudioSync() {
        this(-1, 0.0F);
    }

    public PacketAudioSync(int audioid, float volume) {
        this.id = PacketID.AUDIO_SYNC.getID();

        this.audioid = audioid;
        this.seconds = volume;
    }

    public PacketAudioSync(int audioid, float volume, double margin) {
        this(audioid, volume);
        this.margin = margin;
    }

    public void setMargin(double margin) {
        this.margin = margin;
    }

    public double getMargin() {
        return this.margin;
    }

    public double getSeconds() {
        return this.seconds;
    }

    public int getAudioID() {
        return this.audioid;
    }

    public PacketAudioSync fromJSON(JsonObject obj) {
        try {
            this.audioid = obj.get("audioid").getAsInt();
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
            obj.addProperty("audioid", this.audioid);
            obj.addProperty("seconds", this.seconds);
            obj.addProperty("margin", this.margin);
        } catch (Exception e) {
            return null;
        }
        return obj;
    }
}
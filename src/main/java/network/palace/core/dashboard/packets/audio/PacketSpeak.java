package network.palace.core.dashboard.packets.audio;

import com.google.gson.JsonObject;
import lombok.Getter;
import network.palace.core.dashboard.packets.BasePacket;
import network.palace.core.dashboard.packets.PacketID;

/**
 * The type Packet speak.
 */
public class PacketSpeak extends BasePacket {

    @Getter private String script = "";

    /**
     * Instantiates a new Packet speak.
     */
    public PacketSpeak() {
        this("");
    }

    /**
     * Instantiates a new Packet speak.
     *
     * @param script the script
     */
    public PacketSpeak(String script) {
        super(PacketID.COMPUTER_SPEAK.getID());
        this.script = script;
    }

    public PacketSpeak fromJSON(JsonObject obj) {
        try {
            this.script = obj.get("voicetext").getAsString();
        } catch (Exception e) {
            return null;
        }
        return this;
    }

    public JsonObject getJSON() {
        JsonObject obj = new JsonObject();
        try {
            obj.addProperty("id", this.id);
            obj.addProperty("voicetext", this.script);
        } catch (Exception e) {
            return null;
        }
        return obj;
    }
}

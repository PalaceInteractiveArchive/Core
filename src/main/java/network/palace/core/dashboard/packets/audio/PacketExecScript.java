package network.palace.core.dashboard.packets.audio;

import com.google.gson.JsonObject;
import lombok.Getter;
import network.palace.core.dashboard.packets.BasePacket;
import network.palace.core.dashboard.packets.PacketID;

/**
 * The type Packet exec script.
 */
public class PacketExecScript extends BasePacket {

    @Getter private String script = "";

    /**
     * Instantiates a new Packet exec script.
     */
    public PacketExecScript() {
        this("");
    }

    /**
     * Instantiates a new Packet exec script.
     *
     * @param script the script
     */
    public PacketExecScript(String script) {
        super(PacketID.EXEC_SCRIPT.getID());
        this.script = script;
    }

    public PacketExecScript fromJSON(JsonObject obj) {
        try {
            this.script = obj.get("script").getAsString();
        } catch (Exception e) {
            return null;
        }
        return this;
    }

    public JsonObject getJSON() {
        JsonObject obj = new JsonObject();
        try {
            obj.addProperty("id", this.id);
            obj.addProperty("script", this.script);
        } catch (Exception e) {
            return null;
        }
        return obj;
    }
}

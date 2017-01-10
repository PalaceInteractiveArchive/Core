package network.palace.core.dashboard.packets.audio;

import com.google.gson.JsonObject;
import lombok.Getter;
import network.palace.core.dashboard.packets.BasePacket;
import network.palace.core.dashboard.packets.PacketID;

/**
 * The type Packet warp.
 */
public class PacketWarp extends BasePacket {

    @Getter private String warpName = "";

    /**
     * Instantiates a new Packet warp.
     */
    public PacketWarp() {
        this("");
    }

    /**
     * Instantiates a new Packet warp.
     *
     * @param warp the warp
     */
    public PacketWarp(String warp) {
        super(PacketID.INCOMING_WARP.getID());
        this.warpName = warp;
    }

    public PacketWarp fromJSON(JsonObject obj) {
        try {
            this.warpName = obj.get("warp").getAsString();
        } catch (Exception e) {
            return null;
        }
        return this;
    }

    public JsonObject getJSON() {
        JsonObject obj = new JsonObject();
        try {
            obj.addProperty("id", this.id);
            obj.addProperty("warp", this.warpName);
        } catch (Exception e) {
            return null;
        }
        return obj;
    }
}

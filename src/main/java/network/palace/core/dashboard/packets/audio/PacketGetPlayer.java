package network.palace.core.dashboard.packets.audio;

import com.google.gson.JsonObject;
import lombok.Getter;
import network.palace.core.dashboard.packets.BasePacket;
import network.palace.core.dashboard.packets.PacketID;

/**
 * The type Packet get player.
 */
public class PacketGetPlayer extends BasePacket {

    @Getter private String playerName = "";

    /**
     * Instantiates a new Packet get player.
     */
    public PacketGetPlayer() {
        this("");
    }

    /**
     * Instantiates a new Packet get player.
     *
     * @param playerName the player name
     */
    public PacketGetPlayer(String playerName) {
        super(PacketID.GETPLAYER.getID());
        this.playerName = playerName;
    }

    public PacketGetPlayer fromJSON(JsonObject obj) {
        this.playerName = obj.get("playername").getAsString();
        return this;
    }

    public JsonObject getJSON() {
        JsonObject obj = new JsonObject();
        try {
            obj.addProperty("id", this.id);
            obj.addProperty("playername", this.playerName);
        } catch (Exception e) {
            return null;
        }
        return obj;
    }
}

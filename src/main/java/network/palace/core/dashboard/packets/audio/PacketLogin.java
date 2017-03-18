package network.palace.core.dashboard.packets.audio;

import com.google.gson.JsonObject;
import lombok.Getter;
import network.palace.core.dashboard.packets.BasePacket;
import network.palace.core.dashboard.packets.PacketID;

/**
 * The type Packet login.
 */
public class PacketLogin extends BasePacket {

    @Getter private int protocolVersion = 7;
    @Getter private String playerName = "";
    @Getter private String auth = "";

    /**
     * Instantiates a new Packet login.
     */
    public PacketLogin() {
        this(0, "", "");
    }

    /**
     * Instantiates a new Packet login.
     *
     * @param protocolVersion the protocol version
     * @param playerName      the player name
     * @param auth            the auth
     */
    public PacketLogin(int protocolVersion, String playerName, String auth) {
        super(PacketID.LOGIN.getID());
        this.protocolVersion = protocolVersion;
        this.playerName = playerName;
        this.auth = auth;
    }

    public PacketLogin fromJSON(JsonObject obj) {
        this.protocolVersion = obj.get("version").getAsInt();
        this.playerName = obj.get("playername").getAsString();
        this.auth = obj.get("auth").getAsString();
        return this;
    }

    public JsonObject getJSON() {
        JsonObject obj = new JsonObject();
        try {
            obj.addProperty("id", this.id);
            obj.addProperty("version", this.protocolVersion);
            obj.addProperty("playername", this.playerName);
            obj.addProperty("auth", this.auth);
        } catch (Exception e) {
            return null;
        }
        return obj;
    }
}

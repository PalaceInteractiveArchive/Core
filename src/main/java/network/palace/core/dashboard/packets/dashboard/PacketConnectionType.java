package network.palace.core.dashboard.packets.dashboard;

import com.google.gson.JsonObject;
import lombok.Getter;
import network.palace.core.dashboard.packets.BasePacket;
import network.palace.core.dashboard.packets.PacketID;

/**
 * The type Packet connection type.
 */
public class PacketConnectionType extends BasePacket {

    @Getter private ConnectionType type = null;

    /**
     * Instantiates a new Packet connection type.
     *
     * @param type the type
     */
    public PacketConnectionType(ConnectionType type) {
        super(PacketID.Dashboard.CONNECTIONTYPE.getID());
        this.type = type;
    }

    public PacketConnectionType fromJSON(JsonObject obj) {
        this.type = ConnectionType.fromString(obj.get("type").getAsString());
        return this;
    }

    public JsonObject getJSON() {
        JsonObject obj = new JsonObject();
        try {
            obj.addProperty("id", this.id);
            obj.addProperty("type", this.type.name().toLowerCase());
        } catch (Exception e) {
            return null;
        }
        return obj;
    }

    /**
     * The enum Connection type.
     */
    public enum ConnectionType {
        /**
         * Bungeecord connection type.
         */
        BUNGEECORD,
        /**
         * Daemon connection type.
         */
        DAEMON,
        /**
         * Webclient connection type.
         */
        WEBCLIENT,
        /**
         * Instance connection type.
         */
        INSTANCE,
        /**
         * Audioserver connection type.
         */
        AUDIOSERVER;

        /**
         * From string connection type.
         *
         * @param s the s
         * @return the connection type
         */
        public static ConnectionType fromString(String s) {
            switch (s.toLowerCase()) {
                case "bungeecord":
                    return BUNGEECORD;
                case "daemon":
                    return DAEMON;
                case "webclient":
                    return WEBCLIENT;
                case "instance":
                    return INSTANCE;
                case "audioserver":
                    return AUDIOSERVER;
            }
            return WEBCLIENT;
        }
    }
}

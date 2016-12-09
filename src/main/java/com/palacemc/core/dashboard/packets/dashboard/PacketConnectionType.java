package com.palacemc.core.dashboard.packets.dashboard;

import com.google.gson.JsonObject;
import com.palacemc.core.dashboard.packets.BasePacket;
import com.palacemc.core.dashboard.packets.PacketID;

/**
 * Created by Marc on 7/14/16
 */
public class PacketConnectionType extends BasePacket {
    private ConnectionType type;

    public PacketConnectionType() {
        this(null);
    }

    public PacketConnectionType(ConnectionType type) {
        this.id = PacketID.Dashboard.CONNECTIONTYPE.getID();
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

    public ConnectionType getType() {
        return type;
    }

    public enum ConnectionType {
        BUNGEECORD, DAEMON, WEBCLIENT, INSTANCE, AUDIOSERVER;

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
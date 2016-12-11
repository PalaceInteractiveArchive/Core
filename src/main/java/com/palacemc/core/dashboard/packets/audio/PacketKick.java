package com.palacemc.core.dashboard.packets.audio;

import com.google.gson.JsonObject;
import com.palacemc.core.dashboard.packets.BasePacket;
import com.palacemc.core.dashboard.packets.PacketID;

/**
 * Created by Marc on 6/15/15
 */
@SuppressWarnings("unused")
public class PacketKick extends BasePacket {
    private String message = "";
    private String reason = "";

    public PacketKick() {
        this("", "");
    }

    public PacketKick(String message) {
        this(message, "");
    }

    public PacketKick(String kickmessage, String reason) {
        this.id = PacketID.KICK.getID();

        this.message = kickmessage;
        this.reason = "DEFAULT";
    }

    public String getMessage() {
        return this.message;
    }

    public String getReason() {
        return this.reason;
    }

    public PacketKick fromJSON(JsonObject obj) {
        try {
            this.reason = obj.get("reason").getAsString();
            this.message = obj.get("message").getAsString();
        } catch (Exception e) {
            return null;
        }
        return this;
    }

    public JsonObject getJSON() {
        JsonObject obj = new JsonObject();
        try {
            obj.addProperty("id", this.id);
            obj.addProperty("message", this.message);
            obj.addProperty("reason", this.reason);
        } catch (Exception e) {
            return null;
        }
        return obj;
    }
}
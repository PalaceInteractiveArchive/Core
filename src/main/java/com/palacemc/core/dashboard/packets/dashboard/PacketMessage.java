package com.palacemc.core.dashboard.packets.dashboard;

import com.google.gson.JsonObject;
import com.palacemc.core.dashboard.packets.BasePacket;
import com.palacemc.core.dashboard.packets.PacketID;

import java.util.UUID;

/**
 * Created by Marc on 7/15/16
 */
public class PacketMessage extends BasePacket {
    private UUID uuid;
    private String message;

    public PacketMessage() {
        this(null, "");
    }

    public PacketMessage(UUID uuid, String message) {
        this.id = PacketID.Dashboard.MESSAGE.getID();
        this.uuid = uuid;
        this.message = message;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public String getMessage() {
        return message;
    }

    public PacketMessage fromJSON(JsonObject obj) {
        try {
            this.uuid = UUID.fromString(obj.get("uuid").getAsString());
        } catch (Exception e) {
            this.uuid = null;
        }
        this.message = obj.get("message").getAsString();
        return this;
    }

    public JsonObject getJSON() {
        JsonObject obj = new JsonObject();
        try {
            obj.addProperty("id", this.id);
            obj.addProperty("uuid", this.uuid.toString());
            obj.addProperty("message", this.message);
        } catch (Exception e) {
            return null;
        }
        return obj;
    }
}
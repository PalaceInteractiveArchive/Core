package com.palacemc.core.dashboard.packets.dashboard;

import com.google.gson.JsonObject;
import com.palacemc.core.dashboard.packets.BasePacket;
import com.palacemc.core.dashboard.packets.PacketID;

import java.util.UUID;

/**
 * Created by Marc on 7/14/16
 */
public class PacketPlayerDisconnect extends BasePacket {
    private UUID uuid;
    private String reason;

    public PacketPlayerDisconnect() {
        this(null, "");
    }

    public PacketPlayerDisconnect(UUID uuid, String reason) {
        this.id = PacketID.Dashboard.PLAYERDISCONNECT.getID();
        this.uuid = uuid;
        this.reason = reason;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public String getReason() {
        return reason;
    }

    public PacketPlayerDisconnect fromJSON(JsonObject obj) {
        try {
            this.uuid = UUID.fromString(obj.get("uuid").getAsString());
        } catch (Exception e) {
            this.uuid = null;
        }
        this.reason = obj.get("reason").getAsString();
        return this;
    }

    public JsonObject getJSON() {
        JsonObject obj = new JsonObject();
        try {
            obj.addProperty("id", this.id);
            obj.addProperty("uuid", this.uuid.toString());
            obj.addProperty("reason", this.reason);
        } catch (Exception e) {
            return null;
        }
        return obj;
    }
}
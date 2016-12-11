package com.palacemc.core.dashboard.packets.dashboard;

import com.google.gson.JsonObject;
import com.palacemc.core.dashboard.packets.BasePacket;
import com.palacemc.core.dashboard.packets.PacketID;

import java.util.UUID;

/**
 * Created by Marc on 7/15/16
 */
@SuppressWarnings("unused")
public class PacketServerSwitch extends BasePacket {
    private UUID uuid;
    private String target;

    public PacketServerSwitch() {
        this(null, "");
    }

    public PacketServerSwitch(UUID uuid, String target) {
        this.id = PacketID.Dashboard.SERVERSWITCH.getID();
        this.uuid = uuid;
        this.target = target;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public String getTarget() {
        return target;
    }

    public PacketServerSwitch fromJSON(JsonObject obj) {
        try {
            this.uuid = UUID.fromString(obj.get("uuid").getAsString());
        } catch (Exception e) {
            this.uuid = null;
        }
        this.target = obj.get("target").getAsString();
        return this;
    }

    public JsonObject getJSON() {
        JsonObject obj = new JsonObject();
        try {
            obj.addProperty("id", this.id);
            obj.addProperty("uuid", this.uuid.toString());
            obj.addProperty("target", this.target);
        } catch (Exception e) {
            return null;
        }
        return obj;
    }
}
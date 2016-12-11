package com.palacemc.core.dashboard.packets.dashboard;

import com.google.gson.JsonObject;
import com.palacemc.core.dashboard.packets.BasePacket;
import com.palacemc.core.dashboard.packets.PacketID;

import java.util.UUID;

/**
 * Created by Marc on 9/17/16
 */
@SuppressWarnings("unused")
public class PacketGetPack extends BasePacket {
    private UUID uuid;
    private String pack;

    public PacketGetPack() {
        this(null, "");
    }

    public PacketGetPack(UUID uuid, String pack) {
        this.id = PacketID.Dashboard.GETPACK.getID();
        this.uuid = uuid;
        this.pack = pack;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public String getPack() {
        return pack;
    }

    public PacketGetPack fromJSON(JsonObject obj) {
        try {
            this.uuid = UUID.fromString(obj.get("uuid").getAsString());
        } catch (Exception e) {
            this.uuid = null;
        }
        this.pack = obj.get("pack").getAsString();
        return this;
    }

    public JsonObject getJSON() {
        JsonObject obj = new JsonObject();
        try {
            obj.addProperty("id", this.id);
            obj.addProperty("uuid", this.uuid.toString());
            obj.addProperty("pack", this.pack);
        } catch (Exception e) {
            return null;
        }
        return obj;
    }
}
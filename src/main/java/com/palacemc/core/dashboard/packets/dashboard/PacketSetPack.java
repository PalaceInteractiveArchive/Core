package com.palacemc.core.dashboard.packets.dashboard;

import com.google.gson.JsonObject;
import com.palacemc.core.dashboard.packets.BasePacket;
import com.palacemc.core.dashboard.packets.PacketID;

import java.util.UUID;

/**
 * Created by Marc on 9/17/16
 */
public class PacketSetPack extends BasePacket {
    private UUID uuid;
    private String pack;

    public PacketSetPack() {
        this(null, "");
    }

    public PacketSetPack(UUID uuid, String pack) {
        this.id = PacketID.Dashboard.SETPACK.getID();
        this.uuid = uuid;
        this.pack = pack == null ? "none" : pack;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public String getPack() {
        return pack;
    }

    public PacketSetPack fromJSON(JsonObject obj) {
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
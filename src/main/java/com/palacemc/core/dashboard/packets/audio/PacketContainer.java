package com.palacemc.core.dashboard.packets.audio;

import com.google.gson.JsonObject;
import com.palacemc.core.dashboard.packets.BasePacket;
import com.palacemc.core.dashboard.packets.PacketID;

import java.util.UUID;

/**
 * Created by Marc on 5/24/16
 */
public class PacketContainer extends BasePacket {
    private UUID uuid;
    private String container;

    public PacketContainer() {
    }

    public PacketContainer(UUID uuid, String container) {
        this.id = PacketID.CONTAINER.getID();
        this.uuid = uuid;
        this.container = container;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public String getContainer() {
        return container;
    }

    public PacketContainer fromJSON(JsonObject obj) {
        try {
            this.uuid = UUID.fromString(obj.get("uuid").getAsString());
        } catch (Exception e) {
            this.uuid = null;
        }
        this.container = obj.get("container").getAsString();
        return this;
    }

    public JsonObject getJSON() {
        JsonObject obj = new JsonObject();
        try {
            obj.addProperty("id", this.id);
            obj.addProperty("uuid", uuid != null ? uuid.toString() : null);
            obj.addProperty("container", this.container);
        } catch (Exception e) {
            return null;
        }
        return obj;
    }
}
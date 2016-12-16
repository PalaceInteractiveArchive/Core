package com.palacemc.core.dashboard.packets.audio;

import com.google.gson.JsonObject;
import com.palacemc.core.dashboard.packets.BasePacket;
import com.palacemc.core.dashboard.packets.PacketID;

/**
 * Created by Marc on 6/15/15
 */
@SuppressWarnings("unused")
public class PacketClientAccept extends BasePacket {
    private String servername = "";

    public PacketClientAccept(String servername) {
        this.id = PacketID.CLIENT_ACCEPTED.getID();

        this.servername = servername;
    }

    public PacketClientAccept fromJSON(JsonObject obj) {
        try {
            this.servername = obj.get("servername").getAsString();
        } catch (Exception e) {
            return null;
        }
        return this;
    }

    public JsonObject getJSON() {
        JsonObject obj = new JsonObject();
        try {
            obj.addProperty("id", this.id);
            obj.addProperty("servername", this.servername);
        } catch (Exception e) {
            return null;
        }
        return obj;
    }
}
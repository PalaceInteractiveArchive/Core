package com.palacemc.core.dashboard.packets.dashboard;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.palacemc.core.dashboard.packets.BasePacket;
import com.palacemc.core.dashboard.packets.PacketID;

/**
 * Created by Marc on 6/15/15
 */
@SuppressWarnings("unused")
public class PacketServerStatus extends BasePacket {
    private int onlineCount;
    private JsonArray serverStatus;

    public PacketServerStatus() {
        this(0, null);
    }

    public PacketServerStatus(int onlineCount, JsonArray serverStatus) {
        this.id = PacketID.Dashboard.SERVERSTATUS.getID();
        this.onlineCount = onlineCount;
        this.serverStatus = serverStatus;
    }

    public JsonArray getServerStatus() {
        return serverStatus;
    }

    public int getOnlineCount() {
        return onlineCount;
    }

    public PacketServerStatus fromJSON(JsonObject obj) {
        this.onlineCount = obj.get("onlineCount").getAsInt();
        this.serverStatus = obj.get("serverStatus").getAsJsonArray();
        return this;
    }

    public JsonObject getJSON() {
        JsonObject obj = new JsonObject();
        try {
            obj.addProperty("id", this.id);
            obj.addProperty("onlineCount", this.onlineCount);
            obj.add("serverStatus", this.serverStatus);
        } catch (Exception e) {
            return null;
        }
        return obj;
    }
}
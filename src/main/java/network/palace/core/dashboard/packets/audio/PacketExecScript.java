package com.palacemc.core.dashboard.packets.audio;

import com.google.gson.JsonObject;
import com.palacemc.core.dashboard.packets.BasePacket;
import com.palacemc.core.dashboard.packets.PacketID;

/**
 * Created by Marc on 6/15/15
 */
@SuppressWarnings("unused")
public class PacketExecScript extends BasePacket {
    private String script = "";

    public PacketExecScript() {
        this("");
    }

    public PacketExecScript(String script) {
        this.id = PacketID.EXEC_SCRIPT.getID();

        this.script = script;
    }

    public String getScript() {
        return this.script;
    }

    public PacketExecScript fromJSON(JsonObject obj) {
        try {
            this.script = obj.get("script").getAsString();
        } catch (Exception e) {
            return null;
        }
        return this;
    }

    public JsonObject getJSON() {
        JsonObject obj = new JsonObject();
        try {
            obj.addProperty("id", this.id);
            obj.addProperty("script", this.script);
        } catch (Exception e) {
            return null;
        }
        return obj;
    }
}
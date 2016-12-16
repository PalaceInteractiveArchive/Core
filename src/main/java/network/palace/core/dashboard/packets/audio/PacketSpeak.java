package com.palacemc.core.dashboard.packets.audio;

import com.google.gson.JsonObject;
import com.palacemc.core.dashboard.packets.BasePacket;
import com.palacemc.core.dashboard.packets.PacketID;

/**
 * Created by Marc on 6/15/15
 */
@SuppressWarnings("unused")
public class PacketSpeak extends BasePacket {
    private String voiceText = "";

    public PacketSpeak() {
        this("");
    }

    public PacketSpeak(String script) {
        this.id = PacketID.COMPUTER_SPEAK.getID();

        this.voiceText = script;
    }

    public String getScript() {
        return this.voiceText;
    }

    public PacketSpeak fromJSON(JsonObject obj) {
        try {
            this.voiceText = obj.get("voicetext").getAsString();
        } catch (Exception e) {
            return null;
        }
        return this;
    }

    public JsonObject getJSON() {
        JsonObject obj = new JsonObject();
        try {
            obj.addProperty("id", this.id);
            obj.addProperty("voicetext", this.voiceText);
        } catch (Exception e) {
            return null;
        }
        return obj;
    }
}
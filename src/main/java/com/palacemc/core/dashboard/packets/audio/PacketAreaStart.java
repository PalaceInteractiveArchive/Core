package com.palacemc.core.dashboard.packets.audio;

import com.google.gson.JsonObject;
import com.palacemc.core.dashboard.packets.BasePacket;
import com.palacemc.core.dashboard.packets.PacketID;

/**
 * Created by Marc on 6/15/15
 */
public class PacketAreaStart extends BasePacket {
    private int audioid = 0;
    private String name = "";
    private float volume = 1.0F;
    private int fadetime = 0;
    private boolean repeat = true;

    public PacketAreaStart() {
        this(-1, "", 1.0F, 0, true);
    }

    public PacketAreaStart(int audioid, String name, float volume, int fadetime, boolean repeat) {
        this.id = PacketID.AREA_START.getID();
        this.audioid = audioid;
        this.name = name;
        this.volume = volume;
        this.fadetime = fadetime;
        this.repeat = repeat;
    }

    public float getVolume() {
        return this.volume;
    }

    public int getAudioID() {
        return this.audioid;
    }

    public int getFadetime() {
        return this.fadetime;
    }

    public String getName() {
        return this.name;
    }

    public PacketAreaStart fromJSON(JsonObject obj) {
        try {
            this.audioid = obj.get("audioid").getAsInt();
            this.name = obj.get("name").getAsString();
            this.volume = obj.get("volume").getAsFloat();
            this.fadetime = obj.get("fadetime").getAsInt();
            this.repeat = obj.get("repeat").getAsBoolean();
        } catch (Exception e) {
            return null;
        }
        return this;
    }

    public JsonObject getJSON() {
        JsonObject obj = new JsonObject();
        try {
            obj.addProperty("id", this.id);
            obj.addProperty("audioid", this.audioid);
            obj.addProperty("name", this.name);
            obj.addProperty("volume", this.volume);
            obj.addProperty("fadetime", this.fadetime);
            obj.addProperty("repeat", this.repeat);
        } catch (Exception e) {
            return null;
        }
        return obj;
    }
}
package com.palacemc.core.dashboard.packets.audio;

import com.google.gson.JsonObject;
import com.palacemc.core.dashboard.packets.BasePacket;
import com.palacemc.core.dashboard.packets.PacketID;

/**
 * Created by Marc on 6/15/15
 */
@SuppressWarnings("unused")
public class PacketPlayOnceGlobal extends BasePacket {
    private int audioid = 0;
    private String name = "";
    private float volume = 1.0F;

    public PacketPlayOnceGlobal() {
        this(-1, "", 1.0F);
    }

    public PacketPlayOnceGlobal(int audioid, String name) {
        this(audioid, name, 1.0F);
    }

    public PacketPlayOnceGlobal(int audioid, String name, float volume) {
        this.id = PacketID.GLOBAL_PLAY_ONCE.getID();

        this.audioid = audioid;
        this.name = name;
        this.volume = volume;
    }

    public float getVolume() {
        return this.volume;
    }

    public int getAudioID() {
        return this.audioid;
    }

    public String getName() {
        return this.name;
    }

    public PacketPlayOnceGlobal fromJSON(JsonObject obj) {
        try {
            this.audioid = obj.get("audioid").getAsInt();
            this.name = obj.get("name").getAsString();
            this.volume = obj.get("volume").getAsFloat();
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
        } catch (Exception e) {
            return null;
        }
        return obj;
    }
}
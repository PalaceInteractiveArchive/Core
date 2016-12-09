package com.palacemc.core.dashboard.packets;

import com.google.gson.JsonObject;

/**
 * Created by Marc on 6/15/15
 */
public class BasePacket {
    protected int id = 0;

    public BasePacket fromJSON(JsonObject obj) {
        return this;
    }

    public int getId() {
        return id;
    }

    public JsonObject getJSON() {
        return null;
    }
}
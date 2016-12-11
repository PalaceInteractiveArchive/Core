package com.palacemc.core.dashboard.packets.audio;

import com.google.gson.JsonObject;
import com.palacemc.core.dashboard.packets.BasePacket;
import com.palacemc.core.dashboard.packets.PacketID;

/**
 * Created by Marc on 6/15/15
 */
@SuppressWarnings("unused")
public class PacketNotification extends BasePacket {
    private String text = "";
    private String body = "";
    private String icon = "";

    public PacketNotification() {
        this("", "", "");
    }

    public PacketNotification(String text, String body, String icon) {
        this.id = PacketID.NOTIFICATION.getID();

        this.text = text;
        this.body = body;
        this.icon = icon;
    }

    public String getText() {
        return this.text;
    }

    public String getBody() {
        return this.body;
    }

    public String getIcon() {
        return this.icon;
    }

    public PacketNotification fromJSON(JsonObject obj) {
        try {
            this.text = obj.get("text").getAsString();
            this.body = obj.get("body").getAsString();
            this.icon = obj.get("icon").getAsString();
        } catch (Exception e) {
            return null;
        }
        return this;
    }

    public JsonObject getJSON() {
        JsonObject obj = new JsonObject();
        try {
            obj.addProperty("id", this.id);
            obj.addProperty("text", this.text);
            obj.addProperty("body", this.body);
            obj.addProperty("icon", this.icon);
        } catch (Exception e) {
            return null;
        }
        return obj;
    }
}
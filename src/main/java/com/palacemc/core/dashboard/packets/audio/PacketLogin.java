package com.palacemc.core.dashboard.packets.audio;

import com.google.gson.JsonObject;
import com.palacemc.core.dashboard.packets.BasePacket;
import com.palacemc.core.dashboard.packets.PacketID;

/**
 * Created by Marc on 6/15/15
 */
public class PacketLogin extends BasePacket {
    private int version = 7;
    private String playername = "";
    private String auth = "";

    public PacketLogin() {
        this(0, "", "");
    }

    public PacketLogin(int version, String playername, String auth) {
        this.id = PacketID.LOGIN.getID();

        this.version = version;
        this.playername = playername;
        this.auth = auth;
    }

    public int getProtocolVersion() {
        return this.version;
    }

    public String getPlayerName() {
        return this.playername;
    }

    public String getAuth() {
        return this.auth;
    }

    public PacketLogin fromJSON(JsonObject obj) {
        this.version = obj.get("version").getAsInt();
        this.playername = obj.get("playername").getAsString();
        this.auth = obj.get("auth").getAsString();
        return this;
    }

    public JsonObject getJSON() {
        JsonObject obj = new JsonObject();
        try {
            obj.addProperty("id", this.id);
            obj.addProperty("version", this.version);
            obj.addProperty("playername", this.playername);
            obj.addProperty("auth", this.auth);
        } catch (Exception e) {
            return null;
        }
        return obj;
    }
}
package com.palacemc.core.dashboard.packets.dashboard;

import com.google.gson.JsonObject;
import com.palacemc.core.dashboard.packets.BasePacket;
import com.palacemc.core.dashboard.packets.PacketID;

/**
 * Created by Marc on 8/25/16
 */
public class PacketAddServer extends BasePacket {
    private String name;
    private String address;
    private int port;

    public PacketAddServer() {
        this("", "", 0);
    }

    public PacketAddServer(String name, String address, int port) {
        this.id = PacketID.Dashboard.ADDSERVER.getID();
        this.name = name;
        this.address = address;
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public PacketAddServer fromJSON(JsonObject obj) {
        this.name = obj.get("name").getAsString();
        this.address = obj.get("address").getAsString();
        this.port = obj.get("port").getAsInt();
        return this;
    }

    public JsonObject getJSON() {
        JsonObject obj = new JsonObject();
        try {
            obj.addProperty("id", this.id);
            obj.addProperty("name", this.name);
            obj.addProperty("address", this.address);
            obj.addProperty("port", this.port);
        } catch (Exception e) {
            return null;
        }
        return obj;
    }
}
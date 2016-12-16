package com.palacemc.core.dashboard.packets.dashboard;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.palacemc.core.dashboard.packets.BasePacket;
import com.palacemc.core.dashboard.packets.PacketID;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Marc on 9/10/16
 */
@SuppressWarnings("unused")
public class PacketIPSeenCommand extends BasePacket {
    private UUID uuid;
    private List<String> usernames = new ArrayList<>();
    private String address;

    public PacketIPSeenCommand() {
        this(null, new ArrayList<>(), "");
    }

    public PacketIPSeenCommand(UUID uuid, List<String> usernames, String address) {
        this.id = PacketID.Dashboard.IPSEENCOMMAND.getID();
        this.uuid = uuid;
        this.usernames = usernames;
        this.address = address;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public List<String> getUsernames() {
        return usernames;
    }

    public String getAddress() {
        return address;
    }

    public PacketIPSeenCommand fromJSON(JsonObject obj) {
        try {
            this.uuid = UUID.fromString(obj.get("uuid").getAsString());
        } catch (Exception e) {
            this.uuid = null;
        }
        JsonArray list = obj.get("usernames").getAsJsonArray();
        for (JsonElement e : list) {
            this.usernames.add(e.getAsString());
        }
        this.address = obj.get("address").getAsString();
        return this;
    }

    public JsonObject getJSON() {
        JsonObject obj = new JsonObject();
        try {
            obj.addProperty("id", this.id);
            obj.addProperty("uuid", this.uuid.toString());
            Gson gson = new Gson();
            obj.add("usernames", gson.toJsonTree(this.usernames).getAsJsonArray());
            obj.addProperty("address", this.address);
        } catch (Exception e) {
            return null;
        }
        return obj;
    }
}
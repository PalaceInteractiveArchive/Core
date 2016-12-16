package network.palace.core.dashboard.packets.dashboard;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import network.palace.core.dashboard.packets.BasePacket;
import network.palace.core.dashboard.packets.PacketID;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marc on 8/24/16
 */
@SuppressWarnings("unused")
public class PacketUpdateMOTD extends BasePacket {
    private String motd;
    private String maintenance;
    private List<String> info = new ArrayList<>();

    public PacketUpdateMOTD() {
        this("", "", new ArrayList<>());
    }

    public PacketUpdateMOTD(String motd, String maintenance, List<String> info) {
        this.id = PacketID.Dashboard.UPDATEMOTD.getID();
        this.motd = motd;
        this.maintenance = maintenance;
        this.info = info;
    }

    public String getMOTD() {
        return motd;
    }

    public String getMaintenance() {
        return maintenance;
    }

    public List<String> getInfo() {
        return info;
    }

    public PacketUpdateMOTD fromJSON(JsonObject obj) {
        this.motd = obj.get("motd").getAsString();
        this.maintenance = obj.get("maintenance").getAsString();
        JsonArray list = obj.get("info").getAsJsonArray();
        for (JsonElement e : list) {
            this.info.add(e.getAsString());
        }
        return this;
    }

    public JsonObject getJSON() {
        JsonObject obj = new JsonObject();
        try {
            obj.addProperty("id", this.id);
            obj.addProperty("motd", this.motd);
            obj.addProperty("maintenance", this.maintenance);
            Gson gson = new Gson();
            obj.add("info", gson.toJsonTree(this.info).getAsJsonArray());
        } catch (Exception e) {
            return null;
        }
        return obj;
    }
}
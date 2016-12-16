package network.palace.core.dashboard.packets.dashboard;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import network.palace.core.dashboard.packets.BasePacket;
import network.palace.core.dashboard.packets.PacketID;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Marc on 9/12/16
 */
@SuppressWarnings("unused")
public class PacketMaintenanceWhitelist extends BasePacket {
    private List<UUID> allowed = new ArrayList<>();

    public PacketMaintenanceWhitelist() {
        this(new ArrayList<>());
    }

    public PacketMaintenanceWhitelist(List<UUID> allowed) {
        this.id = PacketID.Dashboard.MAINTENANCELIST.getID();
        this.allowed = allowed;
    }

    public List<UUID> getAllowed() {
        return allowed;
    }

    public PacketMaintenanceWhitelist fromJSON(JsonObject obj) {
        JsonArray list = obj.get("allowed").getAsJsonArray();
        for (JsonElement e : list) {
            try {
                this.allowed.add(UUID.fromString(e.getAsString()));
            } catch (Exception ignored) {
            }
        }
        return this;
    }

    public JsonObject getJSON() {
        JsonObject obj = new JsonObject();
        try {
            obj.addProperty("id", this.id);
            Gson gson = new Gson();
            obj.add("allowed", gson.toJsonTree(this.allowed).getAsJsonArray());
        } catch (Exception e) {
            return null;
        }
        return obj;
    }
}
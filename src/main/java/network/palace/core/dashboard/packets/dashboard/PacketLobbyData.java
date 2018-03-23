package network.palace.core.dashboard.packets.dashboard;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import network.palace.core.dashboard.packets.BasePacket;
import network.palace.core.dashboard.packets.PacketID;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Marc on 8/22/16
 */
@Getter
public class PacketLobbyData extends BasePacket {
    private int parks = 0;
    private int creative = 0;
    private int arcade = 0;
    private HashMap<String, Integer> hubs = new HashMap<>();

    public PacketLobbyData() {
        this(0, 0, 0, new HashMap<>());
    }

    public PacketLobbyData(int parks, int creative, int arcade, HashMap<String, Integer> hubs) {
        super(PacketID.Dashboard.LOBBYDATA.getID());
        this.parks = parks;
        this.creative = creative;
        this.arcade = arcade;
        this.hubs = hubs;
    }

    public PacketLobbyData fromJSON(JsonObject obj) {
        this.parks = obj.get("parks").getAsInt();
        this.creative = obj.get("creative").getAsInt();
        this.arcade = obj.get("arcade").getAsInt();
        JsonArray hubs = obj.get("hubs").getAsJsonArray();
        for (JsonElement e : hubs) {
            JsonObject o = (JsonObject) e;
            this.hubs.put(o.get("name").getAsString(), o.get("count").getAsInt());
        }
        return this;
    }

    public JsonObject getJSON() {
        JsonObject obj = new JsonObject();
        try {
            obj.addProperty("id", this.id);
            obj.addProperty("parks", this.parks);
            obj.addProperty("creative", this.creative);
            obj.addProperty("arcade", this.arcade);
            JsonArray hubs = new JsonArray();
            for (Map.Entry<String, Integer> entry : this.hubs.entrySet()) {
                JsonObject object = new JsonObject();
                object.addProperty("name", entry.getKey());
                object.addProperty("count", entry.getValue());
                hubs.add(object);
            }
            obj.add("hubs", hubs);
        } catch (Exception e) {
            return null;
        }
        return obj;
    }
}
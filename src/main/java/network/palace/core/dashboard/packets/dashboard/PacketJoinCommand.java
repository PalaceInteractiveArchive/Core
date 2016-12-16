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
 * Created by Marc on 8/26/16
 */
@SuppressWarnings("unused")
public class PacketJoinCommand extends BasePacket {
    private UUID uuid;
    private List<String> servers = new ArrayList<>();

    public PacketJoinCommand() {
        this(null, new ArrayList<>());
    }

    public PacketJoinCommand(UUID uuid, List<String> servers) {
        this.id = PacketID.Dashboard.JOINCOMMAND.getID();
        this.uuid = uuid;
        this.servers = servers;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public List<String> getServers() {
        return servers;
    }

    public PacketJoinCommand fromJSON(JsonObject obj) {
        try {
            this.uuid = UUID.fromString(obj.get("uuid").getAsString());
        } catch (Exception e) {
            this.uuid = null;
        }
        JsonArray list = obj.get("servers").getAsJsonArray();
        for (JsonElement e : list) {
            this.servers.add(e.getAsString());
        }
        return this;
    }

    public JsonObject getJSON() {
        JsonObject obj = new JsonObject();
        try {
            obj.addProperty("id", this.id);
            obj.addProperty("uuid", this.uuid.toString());
            Gson gson = new Gson();
            obj.add("servers", gson.toJsonTree(this.servers).getAsJsonArray());
        } catch (Exception e) {
            return null;
        }
        return obj;
    }
}
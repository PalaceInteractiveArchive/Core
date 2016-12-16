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
 * Created by Marc on 9/3/16
 */
@SuppressWarnings({"CanBeFinal", "unused"})
public class PacketTabComplete extends BasePacket {
    private UUID uuid;
    private String command;
    private List<String> args;
    private List<String> results = new ArrayList<>();

    public PacketTabComplete() {
        this(null, "", new ArrayList<>(), new ArrayList<>());
    }

    public PacketTabComplete(UUID uuid, String command, List<String> args, List<String> results) {
        this.id = PacketID.Dashboard.TABCOMPLETE.getID();
        this.uuid = uuid;
        this.command = command;
        this.args = args;
        this.results = results;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public String getCommand() {
        return command;
    }

    public List<String> getArgs() {
        return args;
    }

    public List<String> getResults() {
        return results;
    }

    public PacketTabComplete fromJSON(JsonObject obj) {
        try {
            this.uuid = UUID.fromString(obj.get("uuid").getAsString());
        } catch (Exception e) {
            this.uuid = null;
        }
        this.command = obj.get("command").getAsString();
        JsonArray args = obj.get("args").getAsJsonArray();
        for (JsonElement e : args) {
            this.args.add(e.getAsString());
        }
        JsonArray list = obj.get("results").getAsJsonArray();
        for (JsonElement e : list) {
            this.results.add(e.getAsString());
        }
        return this;
    }

    public JsonObject getJSON() {
        JsonObject obj = new JsonObject();
        try {
            obj.addProperty("id", this.id);
            obj.addProperty("uuid", this.uuid.toString());
            obj.addProperty("command", this.command);
            Gson gson = new Gson();
            obj.add("args", gson.toJsonTree(this.args).getAsJsonArray());
            obj.add("results", gson.toJsonTree(this.results).getAsJsonArray());
        } catch (Exception e) {
            return null;
        }
        return obj;
    }
}
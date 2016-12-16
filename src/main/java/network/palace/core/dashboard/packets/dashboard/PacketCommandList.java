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
 * Created by Marc on 9/3/16
 */
@SuppressWarnings("unused")
public class PacketCommandList extends BasePacket {
    private List<String> commands = new ArrayList<>();

    public PacketCommandList() {
        this(new ArrayList<>());
    }

    public PacketCommandList(List<String> commands) {
        this.id = PacketID.Dashboard.COMMANDLIST.getID();
        this.commands = commands;
    }

    public List<String> getCommands() {
        return commands;
    }

    public PacketCommandList fromJSON(JsonObject obj) {
        JsonArray list = obj.get("commands").getAsJsonArray();
        for (JsonElement e : list) {
            this.commands.add(e.getAsString());
        }
        return this;
    }

    public JsonObject getJSON() {
        JsonObject obj = new JsonObject();
        try {
            obj.addProperty("id", this.id);
            Gson gson = new Gson();
            obj.add("commands", gson.toJsonTree(this.commands).getAsJsonArray());
        } catch (Exception e) {
            return null;
        }
        return obj;
    }
}
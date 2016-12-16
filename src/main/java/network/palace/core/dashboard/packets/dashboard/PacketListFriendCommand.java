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
 * Created by Marc on 8/22/16
 */
@SuppressWarnings("unused")
public class PacketListFriendCommand extends BasePacket {
    private UUID uuid;
    private int page;
    private int maxpage;
    private List<String> friendlist = new ArrayList<>();

    public PacketListFriendCommand() {
        this(null, 0, 0, new ArrayList<>());
    }

    public PacketListFriendCommand(UUID uuid, int page, int maxpage, List<String> friendlist) {
        this.id = PacketID.Dashboard.LISTFRIENDCOMMAND.getID();
        this.uuid = uuid;
        this.page = page;
        this.maxpage = maxpage;
        this.friendlist = friendlist;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public int getPage() {
        return page;
    }

    public int getMaxPage() {
        return maxpage;
    }

    public List<String> getFriendlist() {
        return friendlist;
    }

    public PacketListFriendCommand fromJSON(JsonObject obj) {
        try {
            this.uuid = UUID.fromString(obj.get("uuid").getAsString());
        } catch (Exception e) {
            this.uuid = null;
        }
        this.page = obj.get("page").getAsInt();
        this.maxpage = obj.get("maxpage").getAsInt();
        JsonArray list = obj.get("friendlist").getAsJsonArray();
        for (JsonElement e : list) {
            this.friendlist.add(e.getAsString());
        }
        return this;
    }

    public JsonObject getJSON() {
        JsonObject obj = new JsonObject();
        try {
            obj.addProperty("id", this.id);
            obj.addProperty("uuid", this.uuid.toString());
            obj.addProperty("page", this.page);
            obj.addProperty("maxpage", this.maxpage);
            Gson gson = new Gson();
            obj.add("friendlist", gson.toJsonTree(this.friendlist).getAsJsonArray());
        } catch (Exception e) {
            return null;
        }
        return obj;
    }
}
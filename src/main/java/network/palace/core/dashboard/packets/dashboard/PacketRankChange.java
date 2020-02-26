package network.palace.core.dashboard.packets.dashboard;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import network.palace.core.dashboard.packets.BasePacket;
import network.palace.core.dashboard.packets.PacketID;
import network.palace.core.player.Rank;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The type Packet rank change.
 */
public class PacketRankChange extends BasePacket {
    private UUID uuid;
    @Getter private Rank rank;
    @Getter private List<String> tags;
    @Getter private String source;

    public PacketRankChange() {
        this(null, Rank.SETTLER, null, "");
    }

    public PacketRankChange(UUID uuid, Rank rank, List<String> tags, String source) {
        super(PacketID.Dashboard.RANKCHANGE.getID());
        this.uuid = uuid;
        this.rank = rank;
        this.tags = tags;
        this.source = source;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public PacketRankChange fromJSON(JsonObject obj) {
        this.id = obj.get("id").getAsInt();
        try {
            this.uuid = UUID.fromString(obj.get("uuid").getAsString());
        } catch (Exception e) {
            this.uuid = null;
        }
        this.rank = Rank.fromString(obj.get("rank").getAsString());
        this.tags = new ArrayList<>();
        for (JsonElement e : obj.get("tags").getAsJsonArray()) {
            tags.add(e.getAsString());
        }
        this.source = obj.get("source").getAsString();
        return this;
    }

    public JsonObject getJSON() {
        JsonObject obj = new JsonObject();
        try {
            obj.addProperty("id", this.id);
            obj.addProperty("uuid", this.uuid.toString());
            obj.addProperty("rank", this.rank.getDBName());
            Gson gson = new Gson();
            obj.add("tags", gson.toJsonTree(this.tags).getAsJsonArray());
            obj.addProperty("source", this.source);
        } catch (Exception e) {
            return null;
        }
        return obj;
    }
}
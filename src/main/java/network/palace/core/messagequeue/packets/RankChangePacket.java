package network.palace.core.messagequeue.packets;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import network.palace.core.player.Rank;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class RankChangePacket extends MQPacket {
    private final UUID uuid;
    private final Rank rank;
    private final List<String> tags;
    private final String source;

    public RankChangePacket(JsonObject object) {
        super(PacketID.Global.RANK_CHANGE.getId(), object);
        this.uuid = UUID.fromString(object.get("uuid").getAsString());
        this.rank = Rank.fromString(object.get("rank").getAsString());
        this.tags = new ArrayList<>();
        for (JsonElement e : object.get("tags").getAsJsonArray()) {
            tags.add(e.getAsString());
        }
        this.source = object.get("source").getAsString();
    }

    public RankChangePacket(UUID uuid, Rank rank, List<String> tags, String source) {
        super(PacketID.Global.RANK_CHANGE.getId(), null);
        this.uuid = uuid;
        this.rank = rank;
        this.tags = tags;
        this.source = source;
    }

    @Override
    public JsonObject getJSON() {
        JsonObject object = getBaseJSON();
        object.addProperty("uuid", uuid.toString());
        object.addProperty("rank", rank.getDBName());
        Gson gson = new Gson();
        object.add("tags", gson.toJsonTree(this.tags).getAsJsonArray());
        object.addProperty("source", source);
        return object;
    }
}
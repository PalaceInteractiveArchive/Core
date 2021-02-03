package network.palace.core.messagequeue.packets;

import com.google.gson.JsonObject;
import lombok.Getter;
import network.palace.core.player.Rank;
import network.palace.core.player.RankTag;

public class MentionByRankPacket extends MQPacket {
    @Getter private final Rank rank;
    @Getter private final RankTag tag;
    @Getter private final boolean exact;

    public MentionByRankPacket(JsonObject object) {
        super(PacketID.Global.MENTIONBYRANK.getId(), object);
        this.rank = object.has("rank") ? Rank.fromString(object.get("rank").getAsString()) : null;
        this.tag = object.has("tag") ? RankTag.fromString(object.get("tag").getAsString()) : null;
        this.exact = object.get("exact").getAsBoolean();
    }

    public MentionByRankPacket(Rank rank, RankTag tag, boolean exact) {
        super(PacketID.Global.MENTIONBYRANK.getId(), null);
        this.rank = rank;
        this.tag = tag;
        this.exact = exact;
    }

    @Override
    public JsonObject getJSON() {
        JsonObject object = getBaseJSON();
        if (rank != null) object.addProperty("rank", rank.getDBName());
        if (tag != null) object.addProperty("tag", tag.getDBName());
        object.addProperty("exact", exact);
        return object;
    }
}

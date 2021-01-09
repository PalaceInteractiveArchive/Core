package network.palace.core.messagequeue.packets;

import com.google.gson.JsonObject;
import lombok.Getter;
import network.palace.core.player.Rank;
import network.palace.core.player.RankTag;

public class MessageByRankPacket extends MQPacket {
    @Getter private final String message;
    @Getter private final Rank rank;
    @Getter private final RankTag tag;
    @Getter private final boolean exact;

    public MessageByRankPacket(JsonObject object) {
        super(PacketID.Global.MESSAGEBYRANK.getId(), object);
        this.message = object.get("message").getAsString();
        this.rank = Rank.fromString(object.get("rank").getAsString());
        this.tag = object.has("tag") ? RankTag.fromString(object.get("tag").getAsString()) : null;
        this.exact = object.get("exact").getAsBoolean();
    }

    public MessageByRankPacket(String message, Rank rank, RankTag tag, boolean exact) {
        super(PacketID.Global.MESSAGEBYRANK.getId(), null);
        this.message = message;
        this.rank = rank;
        this.tag = tag;
        this.exact = exact;
    }

    @Override
    public JsonObject getJSON() {
        JsonObject object = getBaseJSON();
        object.addProperty("message", message);
        object.addProperty("rank", rank.getDBName());
        if (tag != null) object.addProperty("tag", tag.getDBName());
        object.addProperty("exact", exact);
        return object;
    }
}

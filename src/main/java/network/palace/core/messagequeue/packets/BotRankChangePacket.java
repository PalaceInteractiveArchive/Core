package network.palace.core.messagequeue.packets;

import com.google.gson.JsonObject;
import lombok.Getter;

public class BotRankChangePacket extends MQPacket {
    @Getter
    private final String rank, username, discordId, tags;

    public BotRankChangePacket(JsonObject object) {
        super(PacketID.Global.DISCORD_BOT_RANKS.getId(), object);
        this.rank = object.get("rank").getAsString();
        this.username = object.get("username").getAsString();
        this.discordId = object.get("user").getAsString();
        this.tags = object.get("tags").getAsString();
    }

    public BotRankChangePacket(String rank, String username, String discordId, String tags) {
        super(PacketID.Global.DISCORD_BOT_RANKS.getId(), null);
        this.rank = rank;
        this.username = username;
        this.discordId = discordId;
        this.tags = tags;
    }

    @Override
    public JsonObject getJSON() {
        JsonObject object = getBaseJSON();
        object.addProperty("rank", rank);
        object.addProperty("username", username);
        object.addProperty("user", discordId);
        object.addProperty("tags", tags);
        return object;
    }
}

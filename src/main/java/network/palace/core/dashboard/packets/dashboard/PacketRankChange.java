package network.palace.core.dashboard.packets.dashboard;

import com.google.gson.JsonObject;
import lombok.Getter;
import network.palace.core.dashboard.packets.BasePacket;
import network.palace.core.dashboard.packets.PacketID;
import network.palace.core.player.Rank;
import network.palace.core.player.SponsorTier;

import java.util.UUID;

/**
 * The type Packet rank change.
 */
public class PacketRankChange extends BasePacket {
    @Getter private UUID uuid = null;
    @Getter private Rank rank = Rank.SETTLER;
    @Getter private SponsorTier tier = SponsorTier.NONE;
    @Getter private String source = "";

    /**
     * Instantiates a new Packet rank change.
     *
     * @param uuid   the uuid
     * @param rank   the rank
     * @param source the source
     */
    public PacketRankChange(UUID uuid, Rank rank, SponsorTier tier, String source) {
        super(PacketID.Dashboard.RANKCHANGE.getID());
        this.uuid = uuid;
        this.rank = rank;
        this.tier = tier;
        this.source = source;
    }

    public PacketRankChange() {
        super(PacketID.Dashboard.RANKCHANGE.getID());
    }

    public PacketRankChange fromJSON(JsonObject obj) {
        try {
            this.uuid = UUID.fromString(obj.get("uuid").getAsString());
        } catch (Exception e) {
            this.uuid = null;
        }
        this.rank = Rank.fromString(obj.get("rank").getAsString());
        this.tier = SponsorTier.fromString(obj.get("tier").getAsString());
        this.source = obj.get("source").getAsString();
        return this;
    }

    public JsonObject getJSON() {
        JsonObject obj = new JsonObject();
        try {
            obj.addProperty("id", this.id);
            obj.addProperty("uuid", this.uuid.toString());
            obj.addProperty("rank", this.rank.getDBName());
            obj.addProperty("tier", this.tier.getDBName());
            obj.addProperty("source", this.source);
        } catch (Exception e) {
            return null;
        }
        return obj;
    }
}

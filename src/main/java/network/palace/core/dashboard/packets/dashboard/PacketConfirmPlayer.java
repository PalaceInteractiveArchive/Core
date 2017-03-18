package network.palace.core.dashboard.packets.dashboard;

import com.google.gson.JsonObject;
import lombok.Getter;
import network.palace.core.dashboard.packets.BasePacket;
import network.palace.core.dashboard.packets.PacketID;

import java.util.UUID;

/**
 * Created by Marc on 2/11/17.
 */
public class PacketConfirmPlayer extends BasePacket {
    private UUID uuid;
    @Getter private boolean exists;

    public PacketConfirmPlayer() {
        this(null, false);
    }

    public PacketConfirmPlayer(UUID uuid, boolean exists) {
        super(PacketID.Dashboard.CONFIRMPLAYER.getID());
        this.uuid = uuid;
        this.exists = exists;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public PacketConfirmPlayer fromJSON(JsonObject obj) {
        try {
            this.uuid = UUID.fromString(obj.get("uuid").getAsString());
        } catch (Exception e) {
            this.uuid = null;
        }
        this.exists = obj.get("exists").getAsBoolean();
        return this;
    }

    public JsonObject getJSON() {
        JsonObject obj = new JsonObject();
        try {
            obj.addProperty("id", this.id);
            obj.addProperty("uuid", this.uuid.toString());
            obj.addProperty("exists", this.exists);
        } catch (Exception e) {
            return null;
        }
        return obj;
    }
}
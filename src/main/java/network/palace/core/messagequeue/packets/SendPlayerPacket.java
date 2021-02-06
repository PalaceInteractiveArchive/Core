package network.palace.core.messagequeue.packets;

import com.google.gson.JsonObject;
import lombok.Getter;

public class SendPlayerPacket extends MQPacket {
    // Players can be targeded by username, UUID, 'Server:Hub1', or 'all'
    @Getter private final String targetPlayer, targetServer;

    public SendPlayerPacket(JsonObject object) {
        super(PacketID.Global.SEND_PLAYER.getId(), object);
        this.targetPlayer = object.get("targetPlayer").getAsString();
        this.targetServer = object.get("targetServer").getAsString();
    }

    public SendPlayerPacket(String targetPlayer, String targetServer) {
        super(PacketID.Global.SEND_PLAYER.getId(), null);
        this.targetPlayer = targetPlayer;
        this.targetServer = targetServer;
    }

    @Override
    public JsonObject getJSON() {
        JsonObject object = getBaseJSON();
        object.addProperty("targetPlayer", targetPlayer);
        object.addProperty("targetServer", targetServer);
        return object;
    }
}

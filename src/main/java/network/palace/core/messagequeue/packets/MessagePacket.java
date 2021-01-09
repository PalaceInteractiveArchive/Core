package network.palace.core.messagequeue.packets;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class MessagePacket extends MQPacket {
    @Getter private final String message;
    @Getter private final List<UUID> players;

    public MessagePacket(JsonObject object) {
        super(PacketID.Global.MESSAGE.getId(), object);
        this.message = object.get("message").getAsString();
        this.players = new ArrayList<>();
        JsonArray array = object.get("players").getAsJsonArray();
        for (JsonElement e : array) {
            players.add(UUID.fromString(e.getAsString()));
        }
    }

    public MessagePacket(String message, List<UUID> players) {
        super(PacketID.Global.MESSAGE.getId(), null);
        this.message = message;
        this.players = players;
    }

    public MessagePacket(String message, UUID... players) {
        super(PacketID.Global.MESSAGE.getId(), null);
        this.message = message;
        this.players = new ArrayList<>(Arrays.asList(players));
    }

    @Override
    public JsonObject getJSON() {
        JsonObject object = getBaseJSON();
        object.addProperty("message", message);

        JsonArray players = new JsonArray();
        this.players.forEach(p -> players.add(p.toString()));
        object.add("players", players);

        return object;
    }
}

package network.palace.core.messagequeue.packets;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ComponentMessagePacket extends MQPacket {
    @Getter private final String serializedMessage;
    @Getter private final List<UUID> players;

    public ComponentMessagePacket(JsonObject object) {
        super(PacketID.Global.COMPONENTMESSAGE.getId(), object);
        this.serializedMessage = object.get("serializedMessage").getAsString();
        this.players = new ArrayList<>();
        JsonArray players = object.get("players").getAsJsonArray();
        for (JsonElement e : players) {
            this.players.add(UUID.fromString(e.getAsString()));
        }
    }

    public ComponentMessagePacket(BaseComponent[] components, UUID uuid) {
        this(ComponentSerializer.toString(components), uuid);
    }

    public ComponentMessagePacket(String serializedMessage, UUID... players) {
        super(PacketID.Global.COMPONENTMESSAGE.getId(), null);
        this.serializedMessage = serializedMessage;
        this.players = new ArrayList<>(Arrays.asList(players));
    }

    @Override
    public JsonObject getJSON() {
        JsonObject object = getBaseJSON();
        object.addProperty("serializedMessage", serializedMessage);
        Gson gson = new Gson();
        object.add("players", gson.toJsonTree(this.players).getAsJsonArray());
        return object;
    }
}
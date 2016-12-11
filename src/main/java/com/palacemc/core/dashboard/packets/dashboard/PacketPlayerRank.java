package com.palacemc.core.dashboard.packets.dashboard;

import com.google.gson.JsonObject;
import com.palacemc.core.dashboard.packets.BasePacket;
import com.palacemc.core.dashboard.packets.PacketID;
import com.palacemc.core.player.Rank;

import java.util.UUID;

@SuppressWarnings("unused")
public class PacketPlayerRank extends BasePacket {
    private UUID uuid;
    private Rank rank;

    public PacketPlayerRank() {
        this(null, Rank.SETTLER);
    }

    public PacketPlayerRank(UUID uuid, Rank rank) {
        this.id = PacketID.Dashboard.PLAYERRANK.getID();
        this.uuid = uuid;
        this.rank = rank;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public Rank getRank() {
        return rank;
    }

    public PacketPlayerRank fromJSON(JsonObject obj) {
        try {
            this.uuid = UUID.fromString(obj.get("uuid").getAsString());
        } catch (Exception e) {
            this.uuid = null;
        }
        this.rank = Rank.fromString(obj.get("rank").getAsString());
        return this;
    }

    public JsonObject getJSON() {
        JsonObject obj = new JsonObject();
        try {
            obj.addProperty("id", this.id);
            obj.addProperty("uuid", this.uuid.toString());
            obj.addProperty("rank", this.rank.getSqlName());
        } catch (Exception e) {
            return null;
        }
        return obj;
    }
}
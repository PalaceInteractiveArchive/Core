package network.palace.core.dashboard.packets.dashboard;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import network.palace.core.dashboard.packets.BasePacket;
import network.palace.core.dashboard.packets.PacketID;

import java.util.HashMap;
import java.util.Map;

@Getter
public class PacketLogStatistic extends BasePacket {
    private String measurement;
    private HashMap<String, Object> tags;
    private HashMap<String, Object> fields;

    public PacketLogStatistic() {
        this(null, new HashMap<>(), new HashMap<>());
    }

    public PacketLogStatistic(String measurement, HashMap<String, Object> tags, HashMap<String, Object> fields) {
        super(PacketID.Dashboard.LOG_STATISTIC.getID());
        this.measurement = measurement;
        this.tags = tags;
        this.fields = fields;
    }

    public PacketLogStatistic fromJSON(JsonObject obj) {
        this.measurement = obj.get("measurement").getAsString();
        JsonArray tags = obj.getAsJsonArray("tags");
        for (JsonElement e : tags) {
            JsonObject o = (JsonObject) e;
            this.tags.put(o.get("key").getAsString(), o.get("value").getAsString());
        }
        JsonArray fields = obj.getAsJsonArray("fields");
        for (JsonElement e : fields) {
            JsonObject o = (JsonObject) e;
            this.fields.put(o.get("key").getAsString(), o.get("value").getAsString());
        }
        return this;
    }

    public JsonObject getJSON() {
        JsonObject obj = new JsonObject();
        try {
            obj.addProperty("id", this.id);
            obj.addProperty("measurement", measurement);
            JsonArray tags = new JsonArray();
            for (Map.Entry<String, Object> entry : this.tags.entrySet()) {
                JsonObject object = new JsonObject();
                object.addProperty("key", entry.getKey());
                Object value = entry.getValue();
                if (value instanceof Number) {
                    object.addProperty("value", (Number) value);
                } else if (value instanceof Boolean) {
                    object.addProperty("value", (Boolean) value);
                } else if (value instanceof String) {
                    object.addProperty("value", (String) value);
                }
                tags.add(object);
            }
            obj.add("tags", tags);
            JsonArray fields = new JsonArray();
            for (Map.Entry<String, Object> entry : this.fields.entrySet()) {
                JsonObject object = new JsonObject();
                object.addProperty("key", entry.getKey());
                Object value = entry.getValue();
                if (value instanceof Number) {
                    object.addProperty("value", (Number) value);
                } else if (value instanceof Boolean) {
                    object.addProperty("value", (Boolean) value);
                } else if (value instanceof String) {
                    object.addProperty("value", (String) value);
                }
                fields.add(object);
            }
            obj.add("fields", fields);
        } catch (Exception e) {
            return null;
        }
        return obj;
    }
}

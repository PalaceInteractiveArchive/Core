package network.palace.core.messagequeue.packets;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class LogStatisticPacket extends MQPacket {
    private final String measurement;
    private final HashMap<String, Object> tags;
    private final HashMap<String, Object> fields;

    public LogStatisticPacket(JsonObject object) {
        super(PacketID.Global.LOG_STATISTIC.getId(), object);
        this.measurement = object.get("measurement").getAsString();
        this.tags = new HashMap<>();
        JsonArray tags = object.getAsJsonArray("tags");
        for (JsonElement e : tags) {
            JsonObject o = (JsonObject) e;
            this.tags.put(o.get("key").getAsString(), o.get("value").getAsString());
        }
        this.fields = new HashMap<>();
        JsonArray fields = object.getAsJsonArray("fields");
        for (JsonElement e : fields) {
            JsonObject o = (JsonObject) e;
            JsonPrimitive primitive = o.get("value").getAsJsonPrimitive();
            if (primitive.isBoolean()) {
                this.fields.put(o.get("key").getAsString(), o.get("value").getAsBoolean());
            } else if (primitive.isNumber()) {
                this.fields.put(o.get("key").getAsString(), o.get("value").getAsFloat());
            } else if (primitive.isString()) {
                this.fields.put(o.get("key").getAsString(), o.get("value").getAsString());
            }
        }
    }

    public LogStatisticPacket(String measurement, HashMap<String, Object> tags, HashMap<String, Object> fields) {
        super(PacketID.Global.LOG_STATISTIC.getId(), null);
        this.measurement = measurement;
        this.tags = tags;
        this.fields = fields;
    }

    @Override
    public JsonObject getJSON() {
        JsonObject object = getBaseJSON();
        try {
            object.addProperty("measurement", measurement);
            JsonArray tags = new JsonArray();
            for (Map.Entry<String, Object> entry : this.tags.entrySet()) {
                JsonObject obj = new JsonObject();
                obj.addProperty("key", entry.getKey());
                Object value = entry.getValue();
                if (value instanceof Number) {
                    obj.addProperty("value", (Number) value);
                } else if (value instanceof Boolean) {
                    obj.addProperty("value", (Boolean) value);
                } else if (value instanceof String) {
                    obj.addProperty("value", (String) value);
                }
                tags.add(obj);
            }
            object.add("tags", tags);
            JsonArray fields = new JsonArray();
            for (Map.Entry<String, Object> entry : this.fields.entrySet()) {
                JsonObject obj = new JsonObject();
                obj.addProperty("key", entry.getKey());
                Object value = entry.getValue();
                if (value instanceof Number) {
                    obj.addProperty("value", (Number) value);
                } else if (value instanceof Boolean) {
                    obj.addProperty("value", (Boolean) value);
                } else if (value instanceof String) {
                    obj.addProperty("value", (String) value);
                }
                fields.add(obj);
            }
            object.add("fields", fields);
        } catch (Exception e) {
            return null;
        }
        return object;
    }
}

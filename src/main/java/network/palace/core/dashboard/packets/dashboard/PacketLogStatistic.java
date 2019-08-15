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
    private String tableName;
    private HashMap<String, Object> values;

    public PacketLogStatistic() {
        this(null, new HashMap<>());
    }

    public PacketLogStatistic(String tableName, HashMap<String, Object> values) {
        super(PacketID.Dashboard.LOG_STATISTIC.getID());
        this.tableName = tableName;
        this.values = values;
    }

    public PacketLogStatistic fromJSON(JsonObject obj) {
        this.tableName = obj.get("table-name").getAsString();
        JsonArray values = obj.getAsJsonArray("values");
        for (JsonElement e : values) {
            JsonObject o = (JsonObject) e;
            this.values.put(o.get("field-name").getAsString(), o.get("value").getAsString());
        }
        return this;
    }

    public JsonObject getJSON() {
        JsonObject obj = new JsonObject();
        try {
            obj.addProperty("id", this.id);
            obj.addProperty("table-name", tableName);
            JsonArray values = new JsonArray();
            for (Map.Entry<String, Object> entry : this.values.entrySet()) {
                JsonObject object = new JsonObject();
                object.addProperty("field-name", entry.getKey());
                Object value = entry.getValue();
                if (value instanceof Number) {
                    object.addProperty("value", (Number) value);
                } else if (value instanceof Boolean) {
                    object.addProperty("value", (Boolean) value);
                } else if (value instanceof String) {
                    object.addProperty("value", (String) value);
                }
                values.add(object);
            }
            obj.add("values", values);
        } catch (Exception e) {
            return null;
        }
        return obj;
    }
}

package network.palace.core.dashboard.packets.audio;

import com.google.gson.JsonObject;
import lombok.Getter;
import network.palace.core.dashboard.packets.BasePacket;
import network.palace.core.dashboard.packets.PacketID;

/**
 * The type Packet notification.
 */
public class PacketNotification extends BasePacket {

    @Getter private String text = "";
    @Getter private String body = "";
    @Getter private String icon = "";

    /**
     * Instantiates a new Packet notification.
     */
    public PacketNotification() {
        this("", "", "");
    }

    /**
     * Instantiates a new Packet notification.
     *
     * @param text the text
     * @param body the body
     * @param icon the icon
     */
    public PacketNotification(String text, String body, String icon) {
        super(PacketID.NOTIFICATION.getID());
        this.text = text;
        this.body = body;
        this.icon = icon;
    }

    public PacketNotification fromJSON(JsonObject obj) {
        try {
            this.text = obj.get("text").getAsString();
            this.body = obj.get("body").getAsString();
            this.icon = obj.get("icon").getAsString();
        } catch (Exception e) {
            return null;
        }
        return this;
    }

    public JsonObject getJSON() {
        JsonObject obj = new JsonObject();
        try {
            obj.addProperty("id", this.id);
            obj.addProperty("text", this.text);
            obj.addProperty("body", this.body);
            obj.addProperty("icon", this.icon);
        } catch (Exception e) {
            return null;
        }
        return obj;
    }
}

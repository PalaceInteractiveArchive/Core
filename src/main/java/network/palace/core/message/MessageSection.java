package network.palace.core.message;

import org.bukkit.ChatColor;
import org.json.simple.JSONObject;

import java.util.Arrays;

public class MessageSection {

    public final String text;
    public ChatColor color = null;
    public ChatColor[] styles = null;
    public String clickActionName = null;
    public String clickActionData = null;
    public String hoverActionName = null;
    public String hoverActionData = null;

    public MessageSection(final String text) {
        this.text = text;
    }

    public StringBuilder getFriendlyString() {
        StringBuilder builder = new StringBuilder();
        if (color != null) {
            builder.append(color);
        }
        if (styles != null) {
            Arrays.asList(styles).forEach(builder::append);
        }
        builder.append(text);
        return builder;
    }

    public JSONObject getJsonObject() {
        JSONObject json = new JSONObject();
        json.put("text", text);
        if (color != null) {
            json.put("color", color.name().toLowerCase());
        }
        if (styles != null) {
            for (final ChatColor style : styles) {
                json.put(style == ChatColor.UNDERLINE ? "underlined" : style.name().toLowerCase(), true);
            }
        }
        if (clickActionName != null && clickActionData != null) {
            JSONObject clickEvent = new JSONObject();
            clickEvent.put("action", clickActionName);
            clickEvent.put("value", clickActionData);
            json.put("clickEvent", clickEvent);
        }
        if (hoverActionName != null && hoverActionData != null) {
            JSONObject hoverEvent = new JSONObject();
            hoverEvent.put("action", hoverActionName);
            hoverEvent.put("value", hoverActionData);
            json.put("hoverEvent", hoverEvent);
        }
        return json;
    }
}

package network.palace.core.message;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import network.palace.core.player.CPlayer;
import network.palace.core.utils.ItemUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FormattedMessage {

    private final List<MessageSection> messageSections;

    public FormattedMessage(final String firstPartText) {
        messageSections = new ArrayList<>();
        messageSections.add(new MessageSection(firstPartText));
    }

    public FormattedMessage color(final ChatColor color) {
        if (!color.isColor()) {
            throw new IllegalArgumentException(color.name() + " is not a color");
        }
        latest().color = color;
        return this;
    }

    public FormattedMessage style(final ChatColor... styles) {
        for (final ChatColor style : styles) {
            if (!style.isFormat()) {
                throw new IllegalArgumentException(style.name() + " is not a style");
            }
        }
        latest().styles = styles;
        return this;
    }

    public FormattedMessage file(final String path) {
        onClick("open_file", path);
        return this;
    }

    public FormattedMessage link(final String url) {
        onClick("open_url", url);
        return this;
    }

    public FormattedMessage suggest(final String command) {
        onClick("suggest_command", command);
        return this;
    }

    public FormattedMessage command(final String command) {
        onClick("run_command", command);
        return this;
    }

    public FormattedMessage achievementTooltip(final String name) {
        onHover("show_achievement", "achievement." + name);
        return this;
    }

    public FormattedMessage itemTooltip(final String itemJSON) {
        onHover("show_item", itemJSON);
        return this;
    }

    public FormattedMessage multilineTooltip(final String name, final String... lines) {
        ItemStack stack = ItemUtil.create(Material.BOOK, 1, name, Arrays.asList(lines));
        return itemTooltip(stack);
    }

    public FormattedMessage itemTooltip(final ItemStack itemStack) {
        try {
            return itemTooltip(ItemUtil.getFriendlyNBT(itemStack));
        } catch (Exception e) {
            e.printStackTrace();
            return this;
        }
    }

    public FormattedMessage tooltip(final String line) {
        onHover("show_text", line);
        return this;
    }

    public FormattedMessage then(final String obj) {
        messageSections.add(new MessageSection(obj.toString()));
        return this;
    }

    public String toFriendlyString() {
        StringBuilder builder = new StringBuilder();
        if (messageSections.size() != 1) {
            for (final MessageSection part : messageSections) {
                builder.append(part.getFriendlyString());
            }
        } else {
            builder.append(latest().getFriendlyString());
        }
        return builder.toString();
    }

    @SuppressWarnings("unchecked")
    public String toJSONString() {
        JSONObject json = new JSONObject();
        if (messageSections.size() != 1) {
            json.put("text", "");
            if (!json.containsKey("extra")) {
                json.put("extra", new JSONArray());
            }
            JSONArray extra = (JSONArray) json.get("extra");
            for (final MessageSection part : messageSections) {
                extra.add(part.getJsonObject());
            }
        } else {
            json = latest().getJsonObject();
        }
        return json.toString();
    }

    public void send(Player player) {
        PacketContainer chatMessage = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.CHAT);
        String json = toJSONString();
        chatMessage.getChatComponents().write(0, WrappedChatComponent.fromJson(json));
        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, chatMessage);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public void send(CPlayer player) {
        send(player.getBukkitPlayer());
    }

    private MessageSection latest() {
        return messageSections.get(messageSections.size() - 1);
    }

    private void onClick(final String name, final String data) {
        final MessageSection latest = latest();
        latest.clickActionName = name;
        latest.clickActionData = data;
    }

    private void onHover(final String name, final String data) {
        final MessageSection latest = latest();
        latest.hoverActionName = name;
        latest.hoverActionData = data;
    }
}

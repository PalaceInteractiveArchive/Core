package network.palace.core.citadel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import network.palace.core.Core;
import network.palace.core.citadel.packets.PacketType;
import network.palace.core.citadel.packets.incoming.PacketIncomingBase;
import network.palace.core.citadel.packets.outgoing.PacketAuthentication;
import network.palace.core.citadel.packets.outgoing.PacketOutgoingBase;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_10;
import org.java_websocket.handshake.ServerHandshake;
import org.reflections.Reflections;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Set;

/**
 * @author Innectic
 * @since 2/12/2017
 */
public class Citadel {

    private WebSocketClient client;
    private final String citadelURI;
    private HashMap<String, Class<? extends PacketIncomingBase>> packets = new HashMap<>();

    public Citadel(String uri) {
        this.client = null;
        this.citadelURI = uri;
        // Create a reflections mapping for the plugin
        Reflections reflections = new Reflections(Core.class.getPackage().getName());
        // Find all commands for the plugin
        Set<Class<? extends PacketIncomingBase>> packetClasses = reflections.getSubTypesOf(PacketIncomingBase.class);
        for (Class<? extends PacketIncomingBase> packetClass : packetClasses) {
            if (!packetClass.isAnnotationPresent(PacketType.class)) return;
            PacketType type = packetClass.getAnnotation(PacketType.class);
            packets.put(type.value(), packetClass);
            Core.logMessage("Citadel", "Registered Packet > " + type.value());
        }
    }

    public void connect() {
        if (citadelURI.trim().equals("")) {
            Core.logMessage("Citadel", ChatColor.DARK_RED + "Provide a url to connect to Citadel with");
            return;
        }

        if (client != null) {
            client.close();
            client = null;
        }
        try {
            client = new WebSocketClient(new URI(citadelURI), new Draft_10()) {
                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                    Core.logMessage("Citadel", "Opened " + citadelURI);
                    sendPacket(new PacketAuthentication("DICKS"));
                }

                @Override
                public void onMessage(String message) {
                    Core.debugCitadelLog(ChatColor.DARK_GREEN + "Incoming " + ChatColor.GRAY +  message);
                    JsonObject object = (JsonObject) new JsonParser().parse(message);
                    if (!object.has("type")) {
                        return;
                    }
                    String type = object.get("type").getAsString();
                    object.remove("type");
                    Class<? extends PacketIncomingBase> classs = packets.get(type);
                    if (classs == null) {
                        Core.logMessage("Citadel", ChatColor.DARK_RED + "Could not find packet type [" + type + "]");
                        return;
                    }
                    try {
                        PacketUtil.getMapper().readValue(object.toString(), classs);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onClose(int i, String s, boolean b) {

                }

                @Override
                public void onError(Exception e) {
                    Core.logMessage("Citadel", ChatColor.DARK_RED + "ERROR: " + e.toString());
                }
            };
            client.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void sendPacket(PacketOutgoingBase base) {
        if (!isConnected()) {
            if (!Core.isDashboardAndSqlDisabled()) {
                Bukkit.getLogger().severe("WebSocket disconnected, cannot send packet!");
            }
            return;
        }
        String info = "";
        try {
            info = PacketUtil.getMapper().writeValueAsString(base);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        Core.debugCitadelLog(ChatColor.DARK_GREEN + "Outgoing " + ChatColor.GRAY + info);
        client.send(info);
    }

    protected boolean isConnected() {
        return client != null && client.getConnection() != null && !client.getConnection().isConnecting() && client.getConnection().isOpen();
    }
}

package network.palace.core.dashboard;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import network.palace.core.Core;
import network.palace.core.dashboard.packets.BasePacket;
import network.palace.core.dashboard.packets.dashboard.PacketConnectionType;
import network.palace.core.dashboard.packets.dashboard.PacketMention;
import network.palace.core.dashboard.packets.dashboard.PacketServerName;
import network.palace.core.events.IncomingPacketEvent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_10;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

/**
 * Created by Marc on 5/22/16
 */
@SuppressWarnings("unused")
public class DashboardConnection {
    private WebSocketClient client;
    private boolean hasAttempted = false;

    private Core instance = Core.getPlugin(Core.class);

    private String dashboardURL = instance.getConfig().getString("dashboardURL");

    public DashboardConnection() {
        start();
    }

    private void start() {
        try {
            client = new WebSocketClient(new URI(dashboardURL), new Draft_10()) {
                @Override
                public void onMessage(String message) {
                    JsonObject object = (JsonObject) new JsonParser().parse(message);
                    if (!object.has("id")) {
                        return;
                    }

                    int id = object.get("id").getAsInt();
//                    System.out.println(object.toString());

                    switch (id) {
                        case 50: {
                            PacketMention packet = new PacketMention().fromJSON(object);
                            UUID uuid = packet.getUniqueId();

                            Player player = Bukkit.getPlayer(uuid);

                            if (player == null) {
                                return;
                            }

                            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 50f, 1f);
                            break;
                        }
                    }

                    IncomingPacketEvent event = new IncomingPacketEvent(id, object.toString());
                    Bukkit.getPluginManager().callEvent(event);
                }

                @Override
                public void onOpen(ServerHandshake handshake) {
                    System.out.println("Successfully connected to Dashboard");

                    DashboardConnection.this.send(
                            new PacketConnectionType(PacketConnectionType.ConnectionType.INSTANCE).getJSON().toString());

                    DashboardConnection.this.send(new PacketServerName(instance.getInstanceName()).getJSON().toString());
                    hasAttempted = false;
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    System.out.println(code + " Disconnected from Dashboard! Reconnecting...");

                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            start();
                        }
                    }, 5000L);
                }

                @Override
                public void onError(Exception ex) {
                    System.out.println("Error in Dashboard connection");
                    ex.printStackTrace();
                }

            };
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        client.connect();
    }

    public void send(String s) {
        if (!isConnected()) {
            Bukkit.getLogger().severe("WebSocket disconnected, cannot send packet!");
            return;
        }
        client.send(s);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isConnected() {
        return client != null && client.getConnection() != null && !client.getConnection().isConnecting() && client.getConnection().isOpen();
    }

    public void stop() {
        client.close();
    }

    public void send(BasePacket packet) {
        send(packet.getJSON().toString());
    }
}
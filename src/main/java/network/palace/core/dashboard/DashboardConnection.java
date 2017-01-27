package network.palace.core.dashboard;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import network.palace.core.Core;
import network.palace.core.dashboard.packets.BasePacket;
import network.palace.core.dashboard.packets.dashboard.*;
import network.palace.core.events.CoreOnlineCountUpdate;
import network.palace.core.events.CurrentPackReceivedEvent;
import network.palace.core.events.EconomyUpdateEvent;
import network.palace.core.events.IncomingPacketEvent;
import network.palace.core.player.CPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_10;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

/**
 * The type Dashboard connection.
 */
public class DashboardConnection {

    private WebSocketClient client;
    private final String dashboardURL;

    public DashboardConnection() {
        dashboardURL = Core.getCoreConfig().getString("dashboardURL");
        if (!Core.isDashboardAndSqlDisabled()) start();
    }

    private void start() {
        if (client != null) {
            client = null;
        }
        try {
            client = new WebSocketClient(new URI(dashboardURL), new Draft_10()) {
                @Override
                public void onMessage(String message) {
                    JsonObject object = (JsonObject) new JsonParser().parse(message);
                    if (!object.has("id")) {
                        return;
                    }
                    int id = object.get("id").getAsInt();
                    Core.debugLog("Incoming: " + object.toString());
                    switch (id) {
                        case 41: {
                            PacketOnlineCount packet = new PacketOnlineCount().fromJSON(object);
                            int count = packet.getCount();
                            new CoreOnlineCountUpdate(count).call();
                            break;
                        }
                        case 49: {
                            PacketGetPack packet = new PacketGetPack().fromJSON(object);
                            UUID uuid = packet.getUuid();
                            String pack = packet.getPack();
                            CPlayer player = Core.getPlayerManager().getPlayer(uuid);
                            if (player == null) break;
                            player.setPack(pack);
                            new CurrentPackReceivedEvent(player, pack).call();
                            break;
                        }
                        case 50: {
                            PacketMention packet = new PacketMention().fromJSON(object);
                            UUID uuid = packet.getUniqueId();
                            CPlayer player = Core.getPlayerManager().getPlayer(uuid);
                            if (player == null) {
                                return;
                            }
                            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 50F, 1F);
                            break;
                        }
                        case 67: {
                            PacketUpdateEconomy packet = new PacketUpdateEconomy().fromJSON(object);
                            UUID uuid = packet.getUniqueId();
                            CPlayer player = Core.getPlayerManager().getPlayer(uuid);
                            if (player == null) {
                                return;
                            }
                            int bal = Core.getEconomy().getBalance(player.getUniqueId());
                            int tok = Core.getEconomy().getTokens(player.getUniqueId());
                            new EconomyUpdateEvent(player.getUniqueId(), bal, true).call();
                            new EconomyUpdateEvent(player.getUniqueId(), tok, false).call();
                            break;
                        }
                    }
                    new IncomingPacketEvent(id, object.toString()).call();
                }

                @Override
                public void onOpen(ServerHandshake handshake) {
                    Core.logMessage("Core", ChatColor.DARK_GREEN + "Successfully connected to Dashboard");
                    DashboardConnection.this.send(new PacketConnectionType(PacketConnectionType.ConnectionType.INSTANCE).getJSON().toString());
                    DashboardConnection.this.send(new PacketServerName(Core.getInstanceName()).getJSON().toString());
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    Core.logMessage("Core", ChatColor.RED + String.valueOf(code) + " Disconnected from Dashboard! Reconnecting...");
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            start();
                        }
                    }, 5000L);
                }

                @Override
                public void onError(Exception ex) {
                    Core.logMessage("Core", ChatColor.RED + "Error in Dashboard connection");
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
            if (!Core.isDashboardAndSqlDisabled()) {
                Bukkit.getLogger().severe("WebSocket disconnected, cannot send packet!");
            }
            return;
        }
        Core.debugLog("Outgoing: " + s);
        client.send(s);
    }

    public boolean isConnected() {
        return client != null && client.getConnection() != null && !client.getConnection().isConnecting() && client.getConnection().isOpen();
    }

    public void stop() {
        if (client == null) return;
        client.close();
    }

    public void send(BasePacket packet) {
        send(packet.getJSON().toString());
    }
}

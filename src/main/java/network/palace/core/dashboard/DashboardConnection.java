package network.palace.core.dashboard;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import network.palace.core.Core;
import network.palace.core.dashboard.packets.BasePacket;
import network.palace.core.dashboard.packets.dashboard.*;
import network.palace.core.economy.CurrencyType;
import network.palace.core.events.*;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
                        case 55: {
                            PacketRankChange packet = new PacketRankChange().fromJSON(object);
                            final UUID uuid = packet.getUuid();
                            final Rank rank = packet.getRank();
                            final CPlayer player = Core.getPlayerManager().getPlayer(uuid);
                            if (uuid == null) return;
                            if (rank == null) return;
                            if (player == null) return;

                            player.setRank(rank);
                            Core.getPlayerManager().displayRank(player);
                        }
                        case 67: {
                            PacketUpdateEconomy packet = new PacketUpdateEconomy().fromJSON(object);
                            UUID uuid = packet.getUniqueId();
                            CPlayer player = Core.getPlayerManager().getPlayer(uuid);
                            if (player == null) {
                                return;
                            }
                            int bal = Core.getMongoHandler().getCurrency(player.getUniqueId(), CurrencyType.BALANCE);
                            int tok = Core.getMongoHandler().getCurrency(player.getUniqueId(), CurrencyType.TOKENS);
                            new EconomyUpdateEvent(player.getUniqueId(), bal, CurrencyType.BALANCE).call();
                            new EconomyUpdateEvent(player.getUniqueId(), tok, CurrencyType.TOKENS).call();
                            break;
                        }
                        case 68: {
                            PacketConfirmPlayer packet = new PacketConfirmPlayer().fromJSON(object);
                            if (!packet.isExists()) {
                                Player player = Bukkit.getPlayer(packet.getUniqueId());
                                if (player != null) {
                                    Bukkit.getScheduler().scheduleSyncDelayedTask(Core.getInstance(), () ->
                                            player.kickPlayer(ChatColor.RED + "Your account is not authorized on our network!"));
                                }
                            }
                            break;
                        }
                        case 69: {
                            PacketDisablePlayer packet = new PacketDisablePlayer().fromJSON(object);
                            if (packet.isDisabled()) {
                                if (!Core.getInstance().getDisabledPlayers().contains(packet.getUuid())) {
                                    Core.getInstance().getDisabledPlayers().add(packet.getUuid());
                                }
                            } else {
                                if (Core.getInstance().getDisabledPlayers().contains(packet.getUuid())) {
                                    Core.getInstance().getDisabledPlayers().remove(packet.getUuid());
                                }
                            }
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
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Core.getInstance(), new Runnable() {
                        @Override
                        public void run() {
                            new DashboardConnectEvent().call();
                        }
                    }, 0L);
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

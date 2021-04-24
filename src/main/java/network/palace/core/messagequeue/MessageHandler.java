package network.palace.core.messagequeue;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.rabbitmq.client.*;
import net.md_5.bungee.api.ChatColor;
import network.palace.core.Core;
import network.palace.core.events.IncomingMessageEvent;
import network.palace.core.messagequeue.packets.*;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

public class MessageHandler {
    public static final AMQP.BasicProperties JSON_PROPS = new AMQP.BasicProperties.Builder().contentEncoding("application/json").build();

    public Connection PUBLISHING_CONNECTION, CONSUMING_CONNECTION;
    public MessageClient ALL_PROXIES, ALL_MC, STATISTICS, PROXY_DIRECT, MC_DIRECT, BOT;

    public final HashMap<String, MessageClient> permanentClients = new HashMap<>();

    private final ConnectionFactory factory;
    private final HashMap<String, Channel> channels = new HashMap<>();

    public MessageHandler() throws IOException, TimeoutException {
        factory = new ConnectionFactory();
        ConfigurationSection section = Core.getCoreConfig().getConfigurationSection("rabbitmq");
        factory.setVirtualHost(section.getString("virtualhost"));
        factory.setHost(section.getString("host"));
        factory.setUsername(section.getString("username"));
        factory.setPassword(section.getString("password"));

        PUBLISHING_CONNECTION = factory.newConnection();
        CONSUMING_CONNECTION = factory.newConnection();

        PUBLISHING_CONNECTION.addShutdownListener(e -> {
            Core.getInstance().getLogger().warning("Publishing connection has been closed - reinitializing!");
            try {
                PUBLISHING_CONNECTION = factory.newConnection();
            } catch (IOException | TimeoutException ioException) {
                Core.getInstance().getLogger().severe("Failed to reinitialize publishing connection!");
                ioException.printStackTrace();
            }
        });
        CONSUMING_CONNECTION.addShutdownListener(e -> {
            Core.getInstance().getLogger().warning("Consuming connection has been closed - reinitializing!");
            try {
                CONSUMING_CONNECTION = factory.newConnection();
            } catch (IOException | TimeoutException ioException) {
                Core.getInstance().getLogger().severe("Failed to reinitialize consuming connection!");
                ioException.printStackTrace();
            }
        });
    }

    public void initialize() throws IOException, TimeoutException {
        try {
            ALL_PROXIES = new MessageClient(ConnectionType.PUBLISHING, "all_proxies", "fanout");
            ALL_MC = new MessageClient(ConnectionType.PUBLISHING, "all_mc", "fanout");
            STATISTICS = new MessageClient(ConnectionType.PUBLISHING, "statistics", true);
            PROXY_DIRECT = new MessageClient(ConnectionType.PUBLISHING, "proxy_direct", "direct");
            MC_DIRECT = new MessageClient(ConnectionType.PUBLISHING, "mc_direct", "direct");
            BOT = new MessageClient(ConnectionType.PUBLISHING, "bot-networking", true);
        } catch (Exception e) {
            e.printStackTrace();
            Core.getInstance().getLogger().severe("There was an error initializing essential message publishing queues!");
        }

        CancelCallback doNothing = consumerTag -> {
        };

        registerConsumer("all_mc", "fanout", "", (consumerTag, delivery) -> {
            try {
                JsonObject object = parseDelivery(delivery);
                Core.debugLog(object.toString());
                int id = object.get("id").getAsInt();
                try {
                    Core.runTask(Core.getInstance(), () -> new IncomingMessageEvent(id, object).call());
                } catch (Exception e) {
                    Core.logMessage("MessageHandler", "Error processing IncomingMessageEvent for incoming packet " + object.toString());
                    e.printStackTrace();
                }
            } catch (Exception e) {
                handleError(consumerTag, delivery, e);
            }
        }, doNothing);

        registerConsumer("mc_direct", "direct", Core.getInstanceName(), (consumerTag, delivery) -> {
            try {
                JsonObject object = parseDelivery(delivery);
                Core.debugLog(object.toString());
                int id = object.get("id").getAsInt();
                try {
                    Core.runTask(Core.getInstance(), () -> new IncomingMessageEvent(id, object).call());
                } catch (Exception e) {
                    Core.logMessage("MessageHandler", "Error processing IncomingMessageEvent for incoming packet " + object.toString());
                    e.printStackTrace();
                }
                //noinspection SwitchStatementWithTooFewBranches
                switch (id) {
                    // Mention
                    case 10: {
                        MentionPacket packet = new MentionPacket(object);
                        CPlayer player = Core.getPlayerManager().getPlayer(packet.getUuid());
                        if (player != null)
                            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 50F, 1F);
                    }
                }
            } catch (Exception e) {
                handleError(consumerTag, delivery, e);
            }
        }, doNothing);
    }

    public void handleError(String consumerTag, Delivery delivery, Exception e) {
        Core.getInstance().getLogger().severe("[MessageHandler] Error processing message: " + e.getClass().getName() + " - " + e.getMessage());
        Core.getInstance().getLogger().severe("consumerTag: " + consumerTag);
        Core.getInstance().getLogger().severe("envelope: " + delivery.getEnvelope().toString());
        Core.getInstance().getLogger().severe("body (bytes): " + Arrays.toString(delivery.getBody()));
        Core.getInstance().getLogger().severe("body (string): " + new String(delivery.getBody(), StandardCharsets.UTF_8));
        e.printStackTrace();
    }

    public JsonObject parseDelivery(Delivery delivery) {
        byte[] bytes = delivery.getBody();
        String s = new String(bytes, StandardCharsets.UTF_8);
        JsonObject object = (JsonObject) new JsonParser().parse(s);
        if (!object.has("id")) throw new IllegalArgumentException("Missing 'id' field from message packet");
        return object;
    }

    /**
     * Register a MessageQueue consumer
     *
     * @param exchange        the exchange name
     * @param exchangeType    the exchange type (i.e. fanout)
     * @param deliverCallback what to do when a message is received
     * @param cancelCallback  what to do when the consumer is closed
     * @return the queue name created (used to cancel the consumer)
     * @throws IOException      on IOException
     * @throws TimeoutException on TimeoutException
     */
    public String registerConsumer(String exchange, String exchangeType, String routingKey, DeliverCallback deliverCallback, CancelCallback cancelCallback) throws IOException, TimeoutException {
        Channel channel = CONSUMING_CONNECTION.createChannel();
        channel.exchangeDeclare(exchange, exchangeType);

        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, exchange, routingKey);

        channel.basicConsume(queueName, true, deliverCallback, cancelCallback);

        channels.put(queueName, channel);

        return queueName;
    }

    public void unregisterConsumer(String queueName) throws IOException {
        Channel channel = channels.remove(queueName);
        if (channel != null) {
            channel.basicCancel(queueName);
        }
    }

    public void shutdown() {
        if (ALL_PROXIES != null) {
            try {
                ALL_PROXIES.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (ALL_MC != null) {
            try {
                ALL_MC.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (STATISTICS != null) {
            try {
                STATISTICS.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (PROXY_DIRECT != null) {
            try {
                PROXY_DIRECT.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (MC_DIRECT != null) {
            try {
                MC_DIRECT.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        channels.forEach((queueName, channel) -> {
            try {
                Connection connection = channel.getConnection();
                if (connection.isOpen()) connection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        channels.clear();
    }

    public void sendMessage(MQPacket packet, MessageClient client) throws IOException {
        sendMessage(packet, client, "");
    }

    public void sendMessage(MQPacket packet, MessageClient client, String routingKey) throws IOException {
        client.basicPublish(packet.toBytes(), routingKey);
    }

    public void sendMessage(MQPacket packet, String exchange, String exchangeType, String routingKey) throws Exception {
        MessageClient client = new MessageClient(ConnectionType.PUBLISHING, exchange, exchangeType);
        client.basicPublish(packet.toBytes(), routingKey);
        client.close();
    }

    public void sendStaffMessage(String message) throws Exception {
        MessageByRankPacket packet = new MessageByRankPacket("[" + ChatColor.RED + "STAFF" +
                ChatColor.WHITE + "] " + message, Rank.TRAINEE, null, false, false);
        sendMessage(packet, ALL_PROXIES);
    }

    public void sendMessageToPlayer(UUID uuid, String message) throws Exception {
        sendMessageToPlayer(uuid, message, false);
    }

    public void sendMessageToPlayer(UUID uuid, String message, boolean component) throws Exception {
        CPlayer player = Core.getPlayerManager().getPlayer(uuid);
        if (player != null) {
            player.sendMessage(message);
            return;
        }
        MQPacket packet;
        if (component) {
            packet = new ComponentMessagePacket(message, uuid);
        } else {
            packet = new MessagePacket(message, uuid);
        }
        sendMessage(packet, ALL_PROXIES);
    }

    public void sendToProxy(MQPacket packet, UUID targetProxy) throws Exception {
        sendMessage(packet, PROXY_DIRECT, targetProxy.toString());
    }

    public Connection getConnection(ConnectionType type) throws IOException, TimeoutException {
        switch (type) {
            case PUBLISHING:
                return PUBLISHING_CONNECTION;
            case CONSUMING:
                return CONSUMING_CONNECTION;
            default:
                return factory.newConnection();
        }
    }

    public void sendDirectServerMessage(MQPacket packet, String server) throws IOException {
        sendMessage(packet, MC_DIRECT, server);
    }
}

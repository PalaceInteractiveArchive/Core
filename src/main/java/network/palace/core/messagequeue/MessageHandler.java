package network.palace.core.messagequeue;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.rabbitmq.client.*;
import net.md_5.bungee.api.ChatColor;
import network.palace.core.Core;
import network.palace.core.messagequeue.packets.MQPacket;
import network.palace.core.messagequeue.packets.MentionPacket;
import network.palace.core.messagequeue.packets.MessageByRankPacket;
import network.palace.core.messagequeue.packets.MessagePacket;
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
    private final ConnectionFactory factory;
    private final HashMap<String, Channel> channels = new HashMap<>();

    public MessageHandler() throws IOException, TimeoutException {
        factory = new ConnectionFactory();
        ConfigurationSection section = Core.getCoreConfig().getConfigurationSection("rabbitmq");
        factory.setVirtualHost(section.getString("virtualhost"));
        factory.setHost(section.getString("host"));
        factory.setUsername(section.getString("username"));
        factory.setPassword(section.getString("password"));

        CancelCallback doNothing = consumerTag -> {
        };

        registerConsumer("mc_direct", "direct", Core.getInstanceName(), (consumerTag, delivery) -> {
            try {
                JsonObject object = parseDelivery(delivery);
                Core.getInstance().getLogger().severe(object.toString());
                //noinspection SwitchStatementWithTooFewBranches
                switch (object.get("id").getAsInt()) {
                    // Mention
                    case 4: {
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

    private void handleError(String consumerTag, Delivery delivery, Exception e) {
        Core.getInstance().getLogger().severe("[MessageHandler] Error processing message: " + e.getClass().getName() + " - " + e.getMessage());
        Core.getInstance().getLogger().severe("consumerTag: " + consumerTag);
        Core.getInstance().getLogger().severe("envelope: " + delivery.getEnvelope().toString());
        Core.getInstance().getLogger().severe("body (bytes): " + Arrays.toString(delivery.getBody()));
        Core.getInstance().getLogger().severe("body (string): " + new String(delivery.getBody(), StandardCharsets.UTF_8));
        e.printStackTrace();
    }

    private JsonObject parseDelivery(Delivery delivery) {
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
        Connection connection = factory.newConnection();

        Channel channel = connection.createChannel();
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

    public void sendMessage(MQPacket packet, String exchange, String exchangeType) throws Exception {
        sendMessage(packet, exchange, exchangeType, "");
    }

    public void sendMessage(MQPacket packet, String exchange, String exchangeType, String routingKey) throws Exception {
        try (Connection connection = factory.newConnection()) {
            Channel channel = connection.createChannel();
            channel.exchangeDeclare(exchange, exchangeType);
            AMQP.BasicProperties props = new AMQP.BasicProperties.Builder().contentEncoding("application/json").build();
            channel.basicPublish(exchange, routingKey, props, packet.toBytes());
        }
    }

    public void sendStaffMessage(String message) throws Exception {
        MessageByRankPacket packet = new MessageByRankPacket("[" + ChatColor.RED + "STAFF" +
                ChatColor.WHITE + "] " + message, Rank.TRAINEE, null, false);
        sendMessage(packet, "all_proxies", "fanout");
    }

    public void sendMessageToPlayer(UUID uuid, String message) throws Exception {
        CPlayer player = Core.getPlayerManager().getPlayer(uuid);
        if (player != null) {
            player.sendMessage(message);
            return;
        }
        MessagePacket packet = new MessagePacket(message, uuid);
        sendMessage(packet, "all_proxies", "fanout");
    }
}

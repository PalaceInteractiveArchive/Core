package network.palace.core.messagequeue;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import lombok.Getter;
import network.palace.core.Core;

import java.io.IOException;

@Getter
public class MessageClient {
    private final Channel channel;
    private final String name;
    private final boolean queue;

    public MessageClient(ConnectionType type, String exchange, String exchangeType) throws Exception {
        queue = false;
        this.channel = Core.getMessageHandler().getConnection(type).createChannel();
        this.name = exchange;
        channel.exchangeDeclare(exchange, exchangeType);
    }

    public MessageClient(ConnectionType type, String queueName, boolean durable) throws Exception {
        queue = true;
        this.channel = Core.getMessageHandler().getConnection(type).createChannel();
        this.name = queueName;
        // queueName, durable, exclusive, autoDelete, args
        channel.queueDeclare(queueName, durable, false, false, null);
    }

    public void basicPublish(byte[] bytes) throws IOException {
        basicPublish(bytes, "");
    }

    public void basicPublish(byte[] bytes, String routingKey) throws IOException {
        basicPublish(bytes, routingKey, MessageHandler.JSON_PROPS);
    }

    public void basicPublish(byte[] bytes, String routingKey, AMQP.BasicProperties props) throws IOException {
        if (queue) {
            channel.basicPublish(routingKey, name, props, bytes);
        } else {
            channel.basicPublish(name, routingKey, props, bytes);
        }
    }
}

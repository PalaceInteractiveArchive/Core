package network.palace.core.events;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import network.palace.core.messagequeue.packets.MQPacket;

@AllArgsConstructor
public class IncomingMessageEvent extends CoreEvent {
    @Getter private final int id;
    @Getter private final JsonObject packet;
}

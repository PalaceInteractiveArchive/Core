package network.palace.core.citadel.packets.outgoing;

import network.palace.core.citadel.packets.PacketType;

/**
 * @author Innectic
 * @since 2/13/2017
 */
@PacketType("authentication")
public class PacketAuthentication extends PacketOutgoingBase {

    public PacketAuthentication(String server) {
        super("authentication", server);
    }

    @Override
    public String toString() {
        return "PacketAuthentication [" +
                "type=" + getType() +
                ", server=" + getServer() +
                "]";
    }
}

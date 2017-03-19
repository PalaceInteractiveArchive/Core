package network.palace.core.citadel.packets.outgoing;

import lombok.Getter;
import network.palace.core.citadel.packets.PacketBase;

/**
 * @author Innectic
 * @since 2/12/2017
 */
public abstract class PacketOutgoingBase extends PacketBase {

    @Getter private String server = "";

    public PacketOutgoingBase(String type, String server) {
        super(type);
        this.server = server;
    }
}

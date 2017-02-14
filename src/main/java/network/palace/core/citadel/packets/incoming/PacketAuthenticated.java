package network.palace.core.citadel.packets.incoming;

import com.fasterxml.jackson.annotation.JsonProperty;
import network.palace.core.citadel.packets.PacketType;

/**
 * @author Innectic
 * @since 2/13/2017
 */
@PacketType("authenticated")
public class PacketAuthenticated extends PacketIncomingBase {

    private PacketAuthenticatedData data = null;

    public PacketAuthenticated(@JsonProperty("data") PacketAuthenticatedData data) {
        super("authenticated");
        this.data = data;
    }
}

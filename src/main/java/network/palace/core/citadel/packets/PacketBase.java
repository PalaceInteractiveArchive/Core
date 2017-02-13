package network.palace.core.citadel.packets;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Innectic
 * @since 2/12/2017
 */
@AllArgsConstructor
public abstract class PacketBase {

    @Getter private String type;
    @Getter private String server;

}

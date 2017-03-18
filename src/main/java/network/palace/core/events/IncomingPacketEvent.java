package network.palace.core.events;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * The type Incoming packet event.
 */
@AllArgsConstructor
public class IncomingPacketEvent extends CoreEvent {
    @Getter private int id;
    @Getter private String packet;
}

package network.palace.core.events;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Marc on 9/18/16
 */
@AllArgsConstructor
public class IncomingPacketEvent extends CoreEvent {
    @Getter private int id;
    @Getter private String packet;
}

package network.palace.core.resource;

import lombok.AllArgsConstructor;
import lombok.Getter;
import network.palace.core.events.CoreEvent;
import network.palace.core.player.CPlayer;

/**
 * Created by Marc on 3/18/15
 */
@AllArgsConstructor
public class ResourceStatusEvent extends CoreEvent {
    @Getter private PackStatus status;
    @Getter private CPlayer player;
}

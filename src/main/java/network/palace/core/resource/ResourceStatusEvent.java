package network.palace.core.resource;

import lombok.AllArgsConstructor;
import lombok.Getter;
import network.palace.core.events.CoreEvent;
import network.palace.core.player.CPlayer;

/**
 * The type Resource status event.
 */
@AllArgsConstructor
public class ResourceStatusEvent extends CoreEvent {
    @Getter private PackStatus status;
    @Getter private CPlayer player;
}

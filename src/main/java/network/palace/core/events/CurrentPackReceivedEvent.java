package network.palace.core.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import network.palace.core.player.CPlayer;

/**
 * The type Current pack received event.
 */
@AllArgsConstructor
public class CurrentPackReceivedEvent extends CoreEvent {
    @Getter private CPlayer player;
    @Getter private String pack;
}

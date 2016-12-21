package network.palace.core.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import network.palace.core.player.CPlayer;

/**
 * Created by Marc on 12/21/16.
 */
@AllArgsConstructor
public class CurrentPackReceivedEvent extends CoreEvent {
    @Getter private CPlayer player;
    @Getter private String pack;
}

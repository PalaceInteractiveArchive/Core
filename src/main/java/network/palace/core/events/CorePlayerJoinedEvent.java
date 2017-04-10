package network.palace.core.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import network.palace.core.player.CPlayer;

@AllArgsConstructor
public class CorePlayerJoinedEvent extends CoreEvent {
    @Getter private CPlayer player;
}

package network.palace.core.events;

import network.palace.core.player.CPlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class CorePlayerJoinDelayedEvent extends CoreEvent {
    @Getter private final CPlayer player;
}

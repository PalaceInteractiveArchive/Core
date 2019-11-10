package network.palace.core.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import network.palace.core.player.CPlayer;

@AllArgsConstructor
public class PlayerToggleAllowFlightEvent extends CoreEvent {
    @Getter private CPlayer player;
    @Getter private boolean flightState;
}

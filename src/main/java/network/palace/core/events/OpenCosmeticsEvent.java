package network.palace.core.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import network.palace.core.player.CPlayer;

@Getter
@RequiredArgsConstructor
public class OpenCosmeticsEvent extends CoreEvent {
    private final CPlayer player;
    @Setter private boolean cancelled = true;
}

package com.thepalace.core.events;

import com.thepalace.core.player.CPlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class CorePlayerJoinDelayedEvent extends CoreEvent {

    @Getter private final CPlayer player;
    @Getter @Setter private String joinMessage;

}

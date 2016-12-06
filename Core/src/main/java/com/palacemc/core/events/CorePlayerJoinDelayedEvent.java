package com.palacemc.core.events;

import com.palacemc.core.player.CPlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class CorePlayerJoinDelayedEvent extends CoreEvent {

    @Getter private final CPlayer player;
    @Getter @Setter private String joinMessage;

}

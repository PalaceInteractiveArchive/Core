package com.palacemc.core.events;

import lombok.AllArgsConstructor;
import lombok.Getter;

@SuppressWarnings("unused")
@AllArgsConstructor
public class CoreOnlineCountUpdate extends CoreEvent {
    @Getter private int count;
}

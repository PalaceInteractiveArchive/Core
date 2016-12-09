package com.palacemc.core.events;

import lombok.Getter;

/**
 * Created by Marc on 12/8/16.
 */
public class CoreOnlineCountUpdate extends CoreEvent {
    @Getter
    private int count;

    public CoreOnlineCountUpdate(int count) {
        this.count = count;
    }
}

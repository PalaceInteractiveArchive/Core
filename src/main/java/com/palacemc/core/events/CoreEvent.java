package com.palacemc.core.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CoreEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    @SuppressWarnings("unused")
    public static HandlerList getHandlerList() {
        return handlers;
    }
}

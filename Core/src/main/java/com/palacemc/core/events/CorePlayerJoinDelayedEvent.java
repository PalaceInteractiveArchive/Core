package com.palacemc.core.events;

import com.palacemc.core.player.CPlayer;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CorePlayerJoinDelayedEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    @Getter private final CPlayer player;

    public CorePlayerJoinDelayedEvent(CPlayer player) {
        this.player = player;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}

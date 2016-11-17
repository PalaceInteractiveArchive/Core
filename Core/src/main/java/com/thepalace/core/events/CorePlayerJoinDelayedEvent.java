package com.thepalace.core.events;

import com.thepalace.core.player.CPlayer;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CorePlayerJoinDelayedEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    @Getter private final CPlayer player;
    @Getter @Setter private String joinMessage;

    public CorePlayerJoinDelayedEvent(CPlayer player, String joinMessage) {
        this.player = player;
        this.joinMessage = joinMessage;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}

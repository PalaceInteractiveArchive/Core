package com.palacemc.core.events;

import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by Marc on 9/18/16
 */
public class IncomingPacketEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    @Getter
    private int id;
    @Getter
    private String packet;

    public IncomingPacketEvent(int id, String packet) {
        this.id = id;
        this.packet = packet;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
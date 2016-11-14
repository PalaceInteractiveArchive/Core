package com.palacemc.core.player.impl;

import com.palacemc.core.packets.server.WrapperPlayServerChat;
import com.palacemc.core.player.CPlayer;
import com.palacemc.core.player.CPlayerActionBarManager;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

public class CorePlayerActionBarManager implements CPlayerActionBarManager {

    private CPlayer player;

    public CorePlayerActionBarManager(CPlayer player) {
        this.player = player;
    }

    @Override
    public void show(String message) {
        WrapperPlayServerChat packet = new WrapperPlayServerChat();
        packet.setPosition(WrapperPlayServerChat.Position.ACTION_BAR);
        packet.setMessage(WrappedChatComponent.fromText(message));
        player.sendPacket(packet);
    }
}

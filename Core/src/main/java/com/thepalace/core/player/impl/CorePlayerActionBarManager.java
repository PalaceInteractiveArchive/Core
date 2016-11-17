package com.thepalace.core.player.impl;

import com.thepalace.core.packets.server.WrapperPlayServerChat;
import com.thepalace.core.player.CPlayer;
import com.thepalace.core.player.CPlayerActionBarManager;
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

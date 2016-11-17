package com.thepalace.core.player.impl;

import com.thepalace.core.packets.server.WrapperPlayServerResourcePackSend;
import com.thepalace.core.player.CPlayer;
import com.thepalace.core.player.CPlayerResourcePackManager;

public class CorePlayerResourcePackManager implements CPlayerResourcePackManager {

    private CPlayer player;

    public CorePlayerResourcePackManager(CPlayer player) {
        this.player = player;
    }

    @Override
    public void send(String url) {
        send(url, "null");
    }

    @Override
    public void send(String url, String hash) {
        WrapperPlayServerResourcePackSend packet = new WrapperPlayServerResourcePackSend();
        packet.setUrl(url);
        packet.setHash(hash);
        player.sendPacket(packet);
    }
}

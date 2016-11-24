package com.thepalace.core.player.impl;

import com.thepalace.core.packets.server.WrapperPlayServerPlayerListHeaderFooter;
import com.thepalace.core.player.CPlayer;
import com.thepalace.core.player.CPlayerHeaderFooterManager;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

public class CorePlayerHeaderFooterManager implements CPlayerHeaderFooterManager {

    private final CPlayer player;
    private String header = " ";
    private String footer = " ";

    public CorePlayerHeaderFooterManager(CPlayer player) {
        this.player = player;
    }

    @Override
    public void setFooter(String footer) {
        if (footer == null || footer.isEmpty()) {
            this.footer = " ";
            return;
        }
        this.footer = footer;
        update();
    }

    @Override
    public void setHeader(String header) {
        if (header == null || header.isEmpty()) {
            this.header = " ";
            return;
        }
        this.header = header;
        update();
    }

    @Override
    public void hide() {
        this.header = " ";
        this.footer = " ";
        update();
    }

    @Override
    public void update() {
        WrapperPlayServerPlayerListHeaderFooter packet = new WrapperPlayServerPlayerListHeaderFooter();
        packet.setHeader(WrappedChatComponent.fromText(header));
        packet.setFooter(WrappedChatComponent.fromText(footer));
        player.sendPacket(packet);
    }
}

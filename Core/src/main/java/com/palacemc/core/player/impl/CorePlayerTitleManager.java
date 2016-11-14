package com.palacemc.core.player.impl;

import com.palacemc.core.packets.server.WrapperPlayServerTitle;
import com.palacemc.core.player.CPlayer;
import com.palacemc.core.player.CPlayerTitleManager;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

public class CorePlayerTitleManager implements CPlayerTitleManager {

    private CPlayer player;

    public CorePlayerTitleManager(CPlayer player) {
        this.player = player;
    }

    @Override
    public void show(String title, String subtitle) {
        show(title, subtitle, 1, 3, 1);
    }

    @Override
    public void show(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        sendTitleWrapper(title, fadeIn, stay, fadeOut, EnumWrappers.TitleAction.TITLE);
        sendTitleWrapper(subtitle, fadeIn, stay, fadeOut, EnumWrappers.TitleAction.SUBTITLE);
    }

    @Override
    public void hide() {
        sendTitleWrapper(" ", 0, 0, 0, EnumWrappers.TitleAction.RESET);
    }

    private void sendTitleWrapper(String message, int fadeIn, int stay, int fadeOut, EnumWrappers.TitleAction action) {
        WrapperPlayServerTitle packet = new WrapperPlayServerTitle();
        if (message == null || message.isEmpty()) message = " ";
        packet.setFadeIn(fadeIn * 20);
        packet.setStay(stay * 20);
        packet.setFadeOut(fadeOut * 20);
        packet.setTitle(WrappedChatComponent.fromText(message));
        packet.setAction(action);
        player.sendPacket(packet);
    }
}

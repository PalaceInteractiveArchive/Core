package com.palacemc.core.packets.adapters;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.palacemc.core.Core;
import com.palacemc.core.player.CPlayer;

public class SettingsAdapter extends PacketAdapter {

    public SettingsAdapter() {
        super(Core.getInstance(), PacketType.Play.Client.SETTINGS);
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        if (event == null) return;
        if (event.getPlayer() == null) return;
        CPlayer player = Core.getPlayerManager().getPlayer(event.getPlayer());
        if (player == null) return;
        if (event.getPacket() == null) return;
        if (event.getPacket().getStrings() == null) return;
        if (event.getPacket().getStrings().read(0) == null) return;
        player.setLocale(event.getPacket().getStrings().read(0));
    }
}

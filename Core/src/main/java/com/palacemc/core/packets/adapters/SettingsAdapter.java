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
        CPlayer player = Core.getPlayerManager().getPlayer(event.getPlayer());
        player.setLocale(event.getPacket().getStrings().read(0));
    }
}

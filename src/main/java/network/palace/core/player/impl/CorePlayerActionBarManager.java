package network.palace.core.player.impl;

import network.palace.core.packets.server.WrapperPlayServerChat;
import network.palace.core.player.CPlayer;
import network.palace.core.player.CPlayerActionBarManager;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CorePlayerActionBarManager implements CPlayerActionBarManager {

    private final CPlayer player;

    @Override
    public void show(String message) {
        WrapperPlayServerChat packet = new WrapperPlayServerChat();
        packet.setPosition(WrapperPlayServerChat.Position.ACTION_BAR);
        packet.setMessage(WrappedChatComponent.fromText(message));
        player.sendPacket(packet);
    }
}

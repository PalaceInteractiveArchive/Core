package network.palace.core.player.impl.managers;

import com.comphenix.protocol.wrappers.EnumWrappers;
import network.palace.core.packets.server.chat.WrapperPlayServerChat;
import network.palace.core.player.CPlayer;
import network.palace.core.player.CPlayerActionBarManager;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import lombok.AllArgsConstructor;

/**
 * The type Core player action bar manager.
 */
@AllArgsConstructor
public class CorePlayerActionBarManager implements CPlayerActionBarManager {

    private final CPlayer player;

    @Override
    public void show(String message) {
        WrapperPlayServerChat packet = new WrapperPlayServerChat();
        packet.setPosition(EnumWrappers.ChatType.GAME_INFO);
        packet.setMessage(WrappedChatComponent.fromText(message));
        player.sendPacket(packet);
    }
}

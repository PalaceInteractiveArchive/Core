package network.palace.core.player.impl.managers;

import network.palace.core.packets.server.title.WrapperPlayServerTitle;
import network.palace.core.player.CPlayer;
import network.palace.core.player.CPlayerTitleManager;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import lombok.AllArgsConstructor;

/**
 * The type Core player title manager.
 */
@AllArgsConstructor
public class CorePlayerTitleManager implements CPlayerTitleManager {

    private final CPlayer player;

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

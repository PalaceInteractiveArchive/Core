package network.palace.core.player.impl.managers;

import network.palace.core.packets.server.WrapperPlayServerResourcePackSend;
import network.palace.core.player.CPlayer;
import network.palace.core.player.CPlayerResourcePackManager;
import lombok.AllArgsConstructor;

/**
 * The type Core player resource pack manager.
 */
@AllArgsConstructor
public class CorePlayerResourcePackManager implements CPlayerResourcePackManager {

    private final CPlayer player;

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

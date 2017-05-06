package network.palace.core.player.impl.managers;

import com.comphenix.protocol.wrappers.WrappedChatComponent;
import network.palace.core.packets.server.playerlist.WrapperPlayServerPlayerListHeaderFooter;
import network.palace.core.player.CPlayer;
import network.palace.core.player.CPlayerHeaderFooterManager;

/**
 * The type Core player header footer manager.
 */
public class CorePlayerHeaderFooterManager implements CPlayerHeaderFooterManager {

    private final CPlayer player;
    private String header = " ";
    private String footer = " ";

    /**
     * Instantiates a new Core player header footer manager.
     *
     * @param player the player
     */
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
    public void setHeaderFooter(String header, String footer) {
        if (header == null || header.isEmpty()) {
            this.header = " ";
        } else {
            this.header = header;
        }
        if (footer == null || footer.isEmpty()) {
            this.footer = " ";
        } else {
            this.footer = footer;
        }
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

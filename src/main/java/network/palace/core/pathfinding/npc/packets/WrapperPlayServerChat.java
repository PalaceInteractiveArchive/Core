package network.palace.core.pathfinding.npc.packets;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

/**
 * @author Innectic
 * @since 1/22/2017
 */
public class WrapperPlayServerChat extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.CHAT;

    public WrapperPlayServerChat() {
        super(new PacketContainer(TYPE), TYPE);
        getContainer().getModifier().writeDefaults();
    }

    public WrapperPlayServerChat(PacketContainer container) {
        super(container, TYPE);
    }

    public WrappedChatComponent getMessage() {
        return getContainer().getChatComponents().read(0);
    }

    public void setMessage(WrappedChatComponent chatComponent) {
        getContainer().getChatComponents().write(0, chatComponent);
    }
}

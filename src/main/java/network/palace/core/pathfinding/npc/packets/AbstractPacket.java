package network.palace.core.pathfinding.npc.packets;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import lombok.Getter;
import network.palace.core.player.CPlayer;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

/**
 * @author Innectic
 * @since 1/21/2017
 */
public class AbstractPacket {
    @Getter private PacketContainer container;

    public AbstractPacket(PacketContainer container, PacketType type) {
        if (container == null) {
            throw new IllegalArgumentException("Packet container can't be null.");
        }
        if (!Objects.equals(container.getType(), type)) {
            throw new IllegalArgumentException(container.getHandle() + " is not type of " + type);
        }

        this.container = container;
    }

    public void sendPacket(CPlayer player) {
        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player.getBukkitPlayer(), getContainer());
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Cannot send packet", e);
        }
    }

    public void revievePacket(CPlayer player) {
        try {
            ProtocolLibrary.getProtocolManager().recieveClientPacket(player.getBukkitPlayer(), getContainer());
        } catch (Exception e) {
            throw new RuntimeException("Cannot revieve packet", e);
        }
    }
}

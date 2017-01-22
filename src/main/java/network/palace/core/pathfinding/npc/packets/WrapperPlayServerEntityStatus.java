package network.palace.core.pathfinding.npc.packets;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import network.palace.core.packets.AbstractPacket;
import org.bukkit.World;
import org.bukkit.entity.Entity;

/**
 * @author Innectic
 * @since 1/22/2017
 */
public class WrapperPlayServerEntityStatus extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.ENTITY_STATUS;

    public WrapperPlayServerEntityStatus() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }

    public WrapperPlayServerEntityStatus(PacketContainer container) {
        super(container, TYPE);
    }

    public int getEntityId() {
        return handle.getIntegers().read(0);
    }

    public void setEntityId(int id) {
        handle.getIntegers().write(0, id);
    }

    public Entity getEntity(World world) {
        return handle.getEntityModifier(world).read(0);
    }

    public Entity getEntity(PacketEvent event) {
        return getEntity(event.getPlayer().getWorld());
    }

    public int getEntityStatus() {
        return handle.getBytes().read(0).intValue();
    }

    public void setEntityStatus(int status) {
        handle.getBytes().write(0, (byte) status);
    }
}
package network.palace.core.pathfinding.npc.packets;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import network.palace.core.packets.AbstractPacket;
import org.bukkit.World;
import org.bukkit.entity.Entity;

/**
 * @author Innectic
 * @since 1/22/2017
 */
public class WrapperPlayClientUseEntity extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Client.USE_ENTITY;

    public WrapperPlayClientUseEntity() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }

    public WrapperPlayClientUseEntity(PacketContainer container) {
        super(container, TYPE);
    }

    public int getTargetId() {
        return handle.getIntegers().read(0);
    }

    public void setTargetId(int id) {
        handle.getIntegers().write(0, id);
    }

    public Entity getTarget(World world) {
        return handle.getEntityModifier(world).read(0);
    }

    public Entity getTarget(PacketEvent event) {
        return getTarget(event.getPlayer().getWorld());
    }

    public EnumWrappers.EntityUseAction getMouse() {
        return handle.getEntityUseActions().read(0);
    }

    public void setMouse(EnumWrappers.EntityUseAction action) {
        handle.getEntityUseActions().write(0, action);
    }
}

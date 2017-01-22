package network.palace.core.pathfinding.npc.packets;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.World;
import org.bukkit.entity.Entity;

/**
 * @author Innectic
 * @since 1/22/2017
 */
public class WrapperPlayServerEntityHeadRotation extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.ENTITY_HEAD_ROTATION;

    public WrapperPlayServerEntityHeadRotation() {
        super(new PacketContainer(TYPE), TYPE);
        getContainer().getModifier().writeDefaults();
    }

    public WrapperPlayServerEntityHeadRotation(PacketContainer container) {
        super(container, TYPE);
    }

    public int getEntityId() {
        return getContainer().getIntegers().read(0);
    }

    public void setEntityId(int id) {
        getContainer().getIntegers().write(0, id);
    }

    public Entity getEntity(World world) {
        return getContainer().getEntityModifier(world).read(0);
    }

    public Entity getEntity(PacketEvent event) {
        return getEntity(event.getPlayer().getWorld());
    }

    public float getHeadYaw() {
        return (getContainer().getBytes().read(0) * 360f) / 256f;
    }

    public void setHeadYaw(float yaw) {
        getContainer().getBytes().write(0, (byte) (yaw * 256f / 360f));
    }

}

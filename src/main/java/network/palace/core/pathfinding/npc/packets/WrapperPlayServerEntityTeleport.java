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
public class WrapperPlayServerEntityTeleport extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.ENTITY_TELEPORT;

    public WrapperPlayServerEntityTeleport() {
        super(new PacketContainer(TYPE), TYPE);
    }

    public WrapperPlayServerEntityTeleport(PacketContainer container) {
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

    public double getX() {
        return getContainer().getIntegers().read(1) / 32D;
    }

    public void setX(double x) {
        getContainer().getIntegers().write(1, (int) Math.floor(x * 32D));
    }

    public double getY() {
        return getContainer().getIntegers().read(2) / 32D;
    }

    public void setY(double y) {
        getContainer().getIntegers().write(2, (int) Math.floor(y * 32D));
    }

    public double getZ() {
        return getContainer().getIntegers().read(3) / 32D;
    }

    public void setZ(double z) {
        getContainer().getIntegers().write(3, (int) Math.floor(z * 32D));
    }

    public float getYaw() {
        return (getContainer().getBytes().read(0) * 360f) / 256f;
    }

    public void setYaw(float yaw) {
        getContainer().getBytes().write(0, (byte) (yaw * 256.0F / 360.0F));
    }

    public float getPitch() {
        return (getContainer().getBytes().read(1) * 360.F) / 256.0F;
    }

    public void setPitch(float pitch) {
        getContainer().getBytes().write(1, (byte) (pitch * 256.0F / 360.0F));
    }
}

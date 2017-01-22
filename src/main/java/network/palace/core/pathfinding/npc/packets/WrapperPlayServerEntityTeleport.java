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
public class WrapperPlayServerEntityTeleport extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.ENTITY_TELEPORT;

    public WrapperPlayServerEntityTeleport() {
        super(new PacketContainer(TYPE), TYPE);
    }

    public WrapperPlayServerEntityTeleport(PacketContainer container) {
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

    public double getX() {
        return handle.getIntegers().read(1) / 32D;
    }

    public void setX(double x) {
        handle.getIntegers().write(1, (int) Math.floor(x * 32D));
    }

    public double getY() {
        return handle.getIntegers().read(2) / 32D;
    }

    public void setY(double y) {
        handle.getIntegers().write(2, (int) Math.floor(y * 32D));
    }

    public double getZ() {
        return handle.getIntegers().read(3) / 32D;
    }

    public void setZ(double z) {
        handle.getIntegers().write(3, (int) Math.floor(z * 32D));
    }

    public float getYaw() {
        return (handle.getBytes().read(0) * 360f) / 256f;
    }

    public void setYaw(float yaw) {
        handle.getBytes().write(0, (byte) (yaw * 256.0F / 360.0F));
    }

    public float getPitch() {
        return (handle.getBytes().read(1) * 360.F) / 256.0F;
    }

    public void setPitch(float pitch) {
        handle.getBytes().write(1, (byte) (pitch * 256.0F / 360.0F));
    }
}

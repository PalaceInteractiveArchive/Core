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
public class WrapperPlayServerEntityVelocity extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.ENTITY_VELOCITY;

    public WrapperPlayServerEntityVelocity() {
        super(new PacketContainer(TYPE), TYPE);
    }

    public WrapperPlayServerEntityVelocity(PacketContainer container) {
        super(container, TYPE);
    }

    public int getEntityId() {
        return handle.getIntegers().read(0);
    }

    public Entity getEntity(World world) {
        return handle.getEntityModifier(world).read(0);
    }

    public void setEntityId(int id) {
        handle.getIntegers().write(0, id);
    }

    public Entity getEntity(PacketEvent event) {
        return getEntity(event.getPlayer().getWorld());
    }

    public double getVelocityX() {
        return handle.getIntegers().read(1) / 8000D;
    }

    public void setVelocityX(double value) {
        handle.getIntegers().write(1, (int) (value * 8000.0D));
    }

    public double getVelocityY() {
        return handle.getIntegers().read(2) / 8000D;
    }

    public void setVelocityY(double value) {
        handle.getIntegers().write(2, (int) (value * 8000.0D));
    }

    public double getVelocityZ() {
        return handle.getIntegers().read(3) / 8000D;
    }

    public void setVelocityZ(double value) {
        handle.getIntegers().write(3, (int) (value * 8000.0D));
    }

}

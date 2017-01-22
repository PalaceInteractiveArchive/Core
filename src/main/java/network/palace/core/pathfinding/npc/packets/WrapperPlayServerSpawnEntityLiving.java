package network.palace.core.pathfinding.npc.packets;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.injector.PacketConstructor;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import network.palace.core.packets.AbstractPacket;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

/**
 * @author Innectic
 * @since 1/22/2017
 */
public class WrapperPlayServerSpawnEntityLiving extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.SPAWN_ENTITY_LIVING;

    private static PacketConstructor entityConstructor;

    public WrapperPlayServerSpawnEntityLiving() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }

    public WrapperPlayServerSpawnEntityLiving(PacketContainer packet) {
        super(packet, TYPE);
    }

    public WrapperPlayServerSpawnEntityLiving(Entity entity) {
        super(fromEntity(entity), TYPE);
    }

    private static PacketContainer fromEntity(Entity entity) {
        if (entityConstructor == null)
            entityConstructor = ProtocolLibrary.getProtocolManager().createPacketConstructor(TYPE, entity);
        return entityConstructor.createPacket(entity);
    }

    public int getEntityID() {
        return handle.getIntegers().read(0);
    }

    public Entity getEntity(World world) {
        return handle.getEntityModifier(world).read(0);
    }

    public Entity getEntity(PacketEvent event) {
        return getEntity(event.getPlayer().getWorld());
    }

    public void setEntityID(int value) {
        handle.getIntegers().write(0, value);
    }

    public EntityType getType() {
        return EntityType.fromId(handle.getIntegers().read(1));
    }

    public void setType(EntityType value) {
        handle.getIntegers().write(1, (int) value.getTypeId());
    }

    public double getX() {
        return handle.getIntegers().read(2) / 32.0D;
    }

    public void setX(double value) {
        handle.getIntegers().write(2, (int) Math.floor(value * 32.0D));
    }

    public double getY() {
        return handle.getIntegers().read(3) / 32.0D;
    }

    public void setY(double value) {
        handle.getIntegers().write(3, (int) Math.floor(value * 32.0D));
    }

    public double getZ() {
        return handle.getIntegers().read(4) / 32.0D;
    }

    public void setZ(double value) {
        handle.getIntegers().write(4, (int) Math.floor(value * 32.0D));
    }

    public float getYaw() {
        return (handle.getBytes().read(0) * 360.F) / 256.0F;
    }

    public void setYaw(float value) {
        handle.getBytes().write(0, (byte) (value * 256.0F / 360.0F));
    }

    public float getHeadPitch() {
        return (handle.getBytes().read(1) * 360.F) / 256.0F;
    }

    public void setHeadPitch(float value) {
        handle.getBytes().write(1, (byte) (value * 256.0F / 360.0F));
    }

    public float getHeadYaw() {
        return (handle.getBytes().read(2) * 360.F) / 256.0F;
    }

    public void setHeadYaw(float value) {
        handle.getBytes().write(2, (byte) (value * 256.0F / 360.0F));
    }

    public double getVelocityX() {
        return handle.getIntegers().read(5) / 8000.0D;
    }

    public void setVelocityX(double value) {
        handle.getIntegers().write(5, (int) (value * 8000.0D));
    }

    public double getVelocityY() {
        return handle.getIntegers().read(6) / 8000.0D;
    }

    public void setVelocityY(double value) {
        handle.getIntegers().write(6, (int) (value * 8000.0D));
    }

    public double getVelocityZ() {
        return handle.getIntegers().read(7) / 8000.0D;
    }

    public void setVelocityZ(double value) {
        handle.getIntegers().write(7, (int) (value * 8000.0D));
    }

    public WrappedDataWatcher getMetadata() {
        return handle.getDataWatcherModifier().read(0);
    }

    public void setMetadata(WrappedDataWatcher value) {
        handle.getDataWatcherModifier().write(0, value);
    }
}

package network.palace.core.pathfinding.npc.packets;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.injector.PacketConstructor;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
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
        getContainer().getModifier().writeDefaults();
    }

    public WrapperPlayServerSpawnEntityLiving(PacketContainer packet) {
        super(packet, TYPE);
    }

    public WrapperPlayServerSpawnEntityLiving(Entity entity) {
        super(fromEntity(entity), TYPE);
    }

    // Useful constructor
    private static PacketContainer fromEntity(Entity entity) {
        if (entityConstructor == null)
            entityConstructor = ProtocolLibrary.getProtocolManager().createPacketConstructor(TYPE, entity);
        return entityConstructor.createPacket(entity);
    }

    public int getEntityID() {
        return getContainer().getIntegers().read(0);
    }

    public Entity getEntity(World world) {
        return getContainer().getEntityModifier(world).read(0);
    }

    public Entity getEntity(PacketEvent event) {
        return getEntity(event.getPlayer().getWorld());
    }

    public void setEntityID(int value) {
        getContainer().getIntegers().write(0, value);
    }

    public EntityType getType() {
        return EntityType.fromId(getContainer().getIntegers().read(1));
    }

    public void setType(EntityType value) {
        getContainer().getIntegers().write(1, (int) value.getTypeId());
    }

    public double getX() {
        return getContainer().getIntegers().read(2) / 32.0D;
    }

    public void setX(double value) {
        getContainer().getIntegers().write(2, (int) Math.floor(value * 32.0D));
    }

    public double getY() {
        return getContainer().getIntegers().read(3) / 32.0D;
    }

    public void setY(double value) {
        getContainer().getIntegers().write(3, (int) Math.floor(value * 32.0D));
    }

    public double getZ() {
        return getContainer().getIntegers().read(4) / 32.0D;
    }

    public void setZ(double value) {
        getContainer().getIntegers().write(4, (int) Math.floor(value * 32.0D));
    }

    public float getYaw() {
        return (getContainer().getBytes().read(0) * 360.F) / 256.0F;
    }

    public void setYaw(float value) {
        getContainer().getBytes().write(0, (byte) (value * 256.0F / 360.0F));
    }

    public float getHeadPitch() {
        return (getContainer().getBytes().read(1) * 360.F) / 256.0F;
    }

    public void setHeadPitch(float value) {
        getContainer().getBytes().write(1, (byte) (value * 256.0F / 360.0F));
    }

    public float getHeadYaw() {
        return (getContainer().getBytes().read(2) * 360.F) / 256.0F;
    }

    public void setHeadYaw(float value) {
        getContainer().getBytes().write(2, (byte) (value * 256.0F / 360.0F));
    }

    public double getVelocityX() {
        return getContainer().getIntegers().read(5) / 8000.0D;
    }

    public void setVelocityX(double value) {
        getContainer().getIntegers().write(5, (int) (value * 8000.0D));
    }

    public double getVelocityY() {
        return getContainer().getIntegers().read(6) / 8000.0D;
    }

    public void setVelocityY(double value) {
        getContainer().getIntegers().write(6, (int) (value * 8000.0D));
    }

    public double getVelocityZ() {
        return getContainer().getIntegers().read(7) / 8000.0D;
    }

    public void setVelocityZ(double value) {
        getContainer().getIntegers().write(7, (int) (value * 8000.0D));
    }

    public WrappedDataWatcher getMetadata() {
        return getContainer().getDataWatcherModifier().read(0);
    }

    public void setMetadata(WrappedDataWatcher value) {
        getContainer().getDataWatcherModifier().write(0, value);
    }
}

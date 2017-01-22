package network.palace.core.pathfinding.npc.packets;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

/**
 * @author Innectic
 * @since 1/22/2017
 */
public class WrapperPlayServerEntityMoveLook extends WrapperPlayServerEntity {
    public static final PacketType TYPE = PacketType.Play.Server.REL_ENTITY_MOVE_LOOK;

    public WrapperPlayServerEntityMoveLook() {
        super(new PacketContainer(TYPE), TYPE);
    }

    public WrapperPlayServerEntityMoveLook(PacketContainer container) {
        super(container, TYPE);
    }

    public double getDx() {
        return handle.getBytes().read(0) / 32D;
    }

    public void setDx(double dx) {
        if (Math.abs(dx) > 4)
            throw new IllegalArgumentException("Displacement cannot be more than 4 meters.");
        handle.getBytes().write(0, (byte) Math.min(Math.floor(dx * 32D), 127));
    }

    public double getDy() {
        return handle.getBytes().read(1) / 32D;
    }

    public void setDy(double dy) {
        if (Math.abs(dy) > 4)
            throw new IllegalArgumentException("Displacement cannot be more than 4 meters.");
        handle.getBytes().write(1, (byte) Math.min(Math.floor(dy * 32D), 127));
    }

    public double getDz() {
        return handle.getBytes().read(2) / 32D;
    }

    public void setDz(double dz) {
        if (Math.abs(dz) > 4)
            throw new IllegalArgumentException("Displacement cannot be more than 4 meters.");
        handle.getBytes().write(2, (byte) Math.min(Math.floor(dz * 32D), 127));
    }

    public float getYaw() {
        return (handle.getBytes().read(3) * 360f) / 256f;
    }

    public void setYaw(float yaw) {
        handle.getBytes().write(3, (byte) (yaw * 256.0F / 360.0F));
    }

    public float getPitch() {
        return (handle.getBytes().read(4) * 360f) / 256f;
    }

    public void setPitch(float value) {
        handle.getBytes().write(4, (byte) (value * 256.0F / 360.0F));
    }
}

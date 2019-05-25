package network.palace.core.npc.entity;

import lombok.Getter;
import network.palace.core.npc.AbstractEntity;
import network.palace.core.packets.AbstractPacket;
import network.palace.core.packets.server.entity.WrapperPlayServerSpawnEntity;
import network.palace.core.pathfinding.Point;
import network.palace.core.player.CPlayer;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.util.Set;
import java.util.UUID;

public class EntityFallingBlock extends AbstractEntity {
    @Getter private final Material material;

    public EntityFallingBlock(Point location, Set<CPlayer> observers, String title, Material material) {
        super(location, observers, title);
        this.material = material;
    }

    @Override
    protected EntityType getEntityType() {
        return EntityType.FALLING_BLOCK;
    }

    @Override
    @SuppressWarnings("deprecation")
    protected AbstractPacket getSpawnPacket() {
        WrapperPlayServerSpawnEntity wrapper = new WrapperPlayServerSpawnEntity();
        wrapper.setType(WrapperPlayServerSpawnEntity.ObjectTypes.FALLING_BLOCK);
        wrapper.setEntityID(entityId);
        wrapper.setUniqueId(UUID.randomUUID());
        wrapper.setX(location.getX());
        wrapper.setY(location.getY());
        wrapper.setZ(location.getZ());
        wrapper.setObjectData(material.getId());
        return wrapper;
    }
}

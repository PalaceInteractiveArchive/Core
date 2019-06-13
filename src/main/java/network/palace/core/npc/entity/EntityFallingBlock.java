package network.palace.core.npc.entity;

import com.comphenix.protocol.utility.MinecraftReflection;
import lombok.Getter;
import network.palace.core.npc.AbstractEntity;
import network.palace.core.packets.AbstractPacket;
import network.palace.core.packets.server.entity.WrapperPlayServerSpawnEntity;
import network.palace.core.pathfinding.Point;
import network.palace.core.player.CPlayer;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.EntityType;

import java.util.Set;
import java.util.UUID;

public class EntityFallingBlock extends AbstractEntity {
    @Getter private int blockData;

    public EntityFallingBlock(Point location, Set<CPlayer> observers, String title, BlockData blockData) {
        super(location, observers, title);
        try {
            Object craftMeta = MinecraftReflection.getCraftBukkitClass("CraftBlockData").cast(blockData);
            Object craftState = craftMeta.getClass().getMethod("getState").invoke(craftMeta);

            Object minecraftBlockClass = MinecraftReflection.getMinecraftClass("Block");
            Object id = minecraftBlockClass.getClass().getMethod("getCombinedId").invoke(craftState);
            this.blockData = (int) id;
        } catch (Exception e) {
            e.printStackTrace();
            this.blockData = 0;
        }
    }

    @Override
    protected EntityType getEntityType() {
        return EntityType.FALLING_BLOCK;
    }

    @Override
    protected AbstractPacket getSpawnPacket() {
        WrapperPlayServerSpawnEntity wrapper = new WrapperPlayServerSpawnEntity();
        wrapper.setType(WrapperPlayServerSpawnEntity.ObjectTypes.FALLING_BLOCK);
        wrapper.setEntityID(entityId);
        wrapper.setUniqueId(UUID.randomUUID());
        wrapper.setX(location.getX());
        wrapper.setY(location.getY());
        wrapper.setZ(location.getZ());
        wrapper.setObjectData(blockData);
        return wrapper;
    }
}

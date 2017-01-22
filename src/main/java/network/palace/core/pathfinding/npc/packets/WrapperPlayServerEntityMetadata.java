package network.palace.core.pathfinding.npc.packets;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.util.List;

/**
 * @author Innectic
 * @since 1/22/2017
 */
public class WrapperPlayServerEntityMetadata extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.ENTITY_METADATA;

    public WrapperPlayServerEntityMetadata() {
        super(new PacketContainer(TYPE), TYPE);
        getContainer().getModifier().writeDefaults();
    }

    public WrapperPlayServerEntityMetadata(PacketContainer container) {
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

    public List<WrappedWatchableObject> getEntityMetadata() {
        return getContainer().getWatchableCollectionModifier().read(0);
    }

    public void setEntityMetadata(List<WrappedWatchableObject> metadata) {
        getContainer().getWatchableCollectionModifier().write(0, metadata);
    }
}

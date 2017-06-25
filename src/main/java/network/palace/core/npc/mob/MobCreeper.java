package network.palace.core.npc.mob;

import lombok.Setter;
import network.palace.core.npc.AbstractMob;
import network.palace.core.npc.ProtocolLibSerializers;
import network.palace.core.pathfinding.Point;
import network.palace.core.player.CPlayer;
import org.bukkit.entity.EntityType;

import java.util.Set;

/**
 * @author Innectic
 * @since 6/25/2017
 */
public class MobCreeper extends AbstractMob {

    @Setter private boolean isCharged = false;
    @Setter private boolean isIgnited = false;

    public MobCreeper(Point location, Set<CPlayer> observers, String title) {
        super(location, observers, title);
    }

    @Override
    protected void onDataWatcherUpdate() {
        int chargedIndex = 12;
        getDataWatcher().setObject(ProtocolLibSerializers.getBoolean(chargedIndex), isCharged);
        int ignitedIndex = 13;
        getDataWatcher().setObject(ProtocolLibSerializers.getBoolean(ignitedIndex), isIgnited);
        super.onDataWatcherUpdate();
    }

    @Override
    protected EntityType getEntityType() {
        return EntityType.CREEPER;
    }

    @Override
    public float getMaximumHealth() {
        return 20f;
    }
}
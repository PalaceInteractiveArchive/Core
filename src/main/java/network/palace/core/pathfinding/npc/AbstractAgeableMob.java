package network.palace.core.pathfinding.npc;

import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import lombok.Setter;
import network.palace.core.pathfinding.Point;
import network.palace.core.player.CPlayer;
import org.bukkit.World;

import java.util.Set;

public abstract class AbstractAgeableMob extends AbstractMob {

    @Setter private boolean baby = false;

    public AbstractAgeableMob(Point location, World world, Set<CPlayer> observers, String title) {
        super(location, world, observers, title);
    }

    @Override
    protected void onDataWatcherUpdate() {
        super.onDataWatcherUpdate();
        WrappedDataWatcher.Serializer booleanSerializer = WrappedDataWatcher.Registry.get(Boolean.class);
        WrappedDataWatcher.WrappedDataWatcherObject adultW = new WrappedDataWatcher.WrappedDataWatcherObject(12, booleanSerializer);
        getDataWatcher().setObject(adultW, baby);
    }
}

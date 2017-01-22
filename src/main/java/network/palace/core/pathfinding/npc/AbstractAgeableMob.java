package network.palace.core.pathfinding.npc;

import lombok.Setter;
import network.palace.core.pathfinding.Point;
import network.palace.core.player.CPlayer;
import org.bukkit.World;

import java.util.Set;

/**
 * @author Innectic
 * @since 1/21/2017
 */
public abstract class AbstractAgeableMob extends AbstractMob {
    @Setter private boolean adult = true;

    public AbstractAgeableMob(Point location, World world, Set<CPlayer> observers, String title) {
        super(location, world, observers, title);
    }

    @Override
    protected void onDataWatcherUpdate() {
        super.onDataWatcherUpdate();
        getDataWatcher().setObject(12, (byte) (adult ? 1 : -1));
    }
}
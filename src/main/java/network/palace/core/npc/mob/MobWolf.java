package network.palace.core.npc.mob;

import network.palace.core.npc.AbstractAnimal;
import network.palace.core.pathfinding.Point;
import network.palace.core.player.CPlayer;
import org.bukkit.World;
import org.bukkit.entity.EntityType;

import java.util.Set;

/**
 * @author Innectic
 * @since 1/27/2017
 */
public class MobWolf extends AbstractAnimal {

    public MobWolf(Point location, World world, Set<CPlayer> observers, String title) {
        super(location, world, observers, title);
    }

    @Override
    protected EntityType getEntityType() {
        return EntityType.WOLF;
    }

    @Override
    public float getMaximumHealth() {
        return 10f;
    }
}

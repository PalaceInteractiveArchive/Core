package network.palace.core.npc.mob;

import network.palace.core.npc.AbstractAnimal;
import network.palace.core.pathfinding.Point;
import network.palace.core.player.CPlayer;
import org.bukkit.entity.EntityType;

import java.util.Set;

/**
 * @author Innectic
 * @since 3/18/2017
 */
public class MobPolarBear extends AbstractAnimal {
    public MobPolarBear(Point point, Set<CPlayer> observers, String title) {
        super(point, observers, title);
    }

    @Override
    protected EntityType getEntityType() {
        return EntityType.POLAR_BEAR;
    }

    @Override
    public float getMaximumHealth() {
        return 30;
    }
}

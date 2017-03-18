package network.palace.core.npc.mob;

import network.palace.core.npc.AbstractAnimal;
import network.palace.core.pathfinding.Point;
import network.palace.core.player.CPlayer;
import org.bukkit.entity.EntityType;

import java.util.Set;

public class MobOcelot extends AbstractAnimal {

    public MobOcelot(Point location, Set<CPlayer> observers, String title) {
        super(location, observers, title);
    }

    @Override
    protected EntityType getEntityType() {
        return EntityType.OCELOT;
    }

    @Override
    public float getMaximumHealth() {
        return 1f;
    }
}

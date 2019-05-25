package network.palace.core.npc;

import network.palace.core.pathfinding.Point;
import network.palace.core.player.CPlayer;

import java.util.Set;

public abstract class AbstractAmbient extends AbstractMob {

    public AbstractAmbient(Point location, Set<CPlayer> observers, String title) {
        super(location, observers, title);
    }
}

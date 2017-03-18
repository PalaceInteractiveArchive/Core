package network.palace.core.npc;

import network.palace.core.pathfinding.Point;
import network.palace.core.player.CPlayer;

import java.util.Set;

public abstract class AbstractAnimal extends AbstractAgeableMob {

    public AbstractAnimal(Point location, Set<CPlayer> observers, String title) {
        super(location, observers, title);
    }

    public void playMateAnimation() {
        playStatus(18);
    }

    public void playMateAnimation(Set<CPlayer> players) {
        playStatus(players, 18);
    }
}

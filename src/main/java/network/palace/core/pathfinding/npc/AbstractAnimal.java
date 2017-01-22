package network.palace.core.pathfinding.npc;

import network.palace.core.pathfinding.Point;
import network.palace.core.player.CPlayer;
import org.bukkit.World;

import java.util.Set;

public abstract class AbstractAnimal extends AbstractAgeableMob {

    public AbstractAnimal(Point location, World world, Set<CPlayer> observers, String title) {
        super(location, world, observers, title);
    }

    public void playMateAnimation() {
        playStatus(18);
    }

    public void playMateAnimation(Set<CPlayer> players) {
        playStatus(players, 18);
    }
}

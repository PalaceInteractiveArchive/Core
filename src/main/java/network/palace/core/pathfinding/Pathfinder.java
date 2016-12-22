package network.palace.core.pathfinding;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

/**
 * @author Innectic
 * @since 12/21/2016
 */
public class Pathfinder {

    public void pathToLocation(Location location, Entity entity) {

    }

    private boolean canWalk(Location location) {
        return true;
    }

    private boolean canWalkForwards(Location location) {
        location.setX(location.getX() + 1);
        return true;
    }

}

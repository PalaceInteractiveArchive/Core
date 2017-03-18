package network.palace.core.vector;

import network.palace.core.Core;
import org.bukkit.Location;

/**
 * @author Innectic
 * @since 3/13/2017
 */
public class Vector {

    private org.bukkit.util.Vector vector;

    /**
     * Create a new vector from a location
     * @param location The location to create a vector from
     */
    public Vector(Location location) {
        this.vector = location.toVector();
    }

    /**
     * Get the lcoation that the vector is based on
     * @return Location of the vector in the world
     */
    public Location getLocation() {
        return this.vector.toLocation(Core.getDefaultWorld());
    }
}

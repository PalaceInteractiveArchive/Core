package network.palace.core.vector;

import lombok.Getter;
import network.palace.core.Core;
import org.bukkit.Location;

/**
 * @author Innectic
 * @since 3/13/2017
 */
public class Vector {

    @Getter private final org.bukkit.util.Vector vector;

    private Vector(Location location) {
        this(location.getX(), location.getY(), location.getZ());
    }

    private Vector(double x, double y, double z) {
        this.vector = new org.bukkit.util.Vector(x, y, z);
    }

    /**
     * Create a new Vector from a location
     *
     * @param location the location to create from
     * @return the vector
     */
    public static Vector of(Location location) {
        return new Vector(location);
    }

    /**
     * Create a new vector from an x y z
     *
     * @param x the x
     * @param y the y
     * @param z the z
     * @return the vector
     */
    public static Vector of(double x, double y, double z) {
        return new Vector(x, y, z);
    }

    /**
     * Create a Core vector from a Bukkit vector
     *
     * @param vector the vector to create from
     * @return the new vector
     */
    public static Vector of(org.bukkit.util.Vector vector) {
        return new Vector(vector.getX(), vector.getY(), vector.getZ());
    }

    /**
     * Get the location that the vector is based on
     *
     * @return Location of the vector in the world
     */
    public Location getLocation() {
        return this.vector.toLocation(Core.getDefaultWorld());
    }
}

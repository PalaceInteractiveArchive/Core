package network.palace.core.utils;

import com.google.common.collect.ImmutableList;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Innectic
 * @since 5/26/2017
 */
public class LocationUtil {

    /**
     * Get the blocks between two locations
     *
     * @param starting the first location
     * @param ending the second location
     * @return all the blocks between the two locations
     */
    public static ImmutableList<Block> getBlocksBetween(Location starting, Location ending) {
        List<Block> blocks = new ArrayList<>();

        // Get the two x's to use
        int x1 = (starting.getBlockX() < ending.getBlockX() ? ending.getBlockX() : starting.getBlockX());
        int x2 = (starting.getBlockX() > ending.getBlockX() ? ending.getBlockX() : starting.getBlockX());

        // The ys
        int y1 = (starting.getBlockY() < ending.getBlockY() ? ending.getBlockY() : starting.getBlockY());
        int y2 = (starting.getBlockY() > ending.getBlockY() ? ending.getBlockY() : starting.getBlockY());

        // The zs
        int z1 = (starting.getBlockZ() < ending.getBlockZ() ? ending.getBlockZ() : starting.getBlockZ());
        int z2 = (starting.getBlockZ() > ending.getBlockZ() ? ending.getBlockZ() : starting.getBlockZ());

        for (int x = x2; x <= x1; x++) {
            for (int z = z2; z <= z1; z++) {
                for (int y = y2; y <= y1; y++) {
                    blocks.add(starting.getWorld().getBlockAt(x, y, z));
                }
            }
        }
        return ImmutableList.copyOf(blocks);
    }

    /**
     * Get the locations between two locations
     *
     * @param starting the first location
     * @param ending the ending location
     * @return all the locations between the two locations
     */
    public static ImmutableList<Location> getLocationsBetween(Location starting, Location ending) {
        return ImmutableList.copyOf((Location[]) getBlocksBetween(starting, ending).stream().map(Block::getLocation).toArray());
    }
}

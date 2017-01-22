package network.palace.core.pathfinding.npc;

import network.palace.core.Core;
import org.bukkit.World;
import org.bukkit.entity.Entity;

/**
 * @author Innectic
 * @since 1/22/2017
 */
public class IdManager {
    private int CURRENT = 2000;

    public int getNextId() {
        do {

        } while (isDuplicate(CURRENT));
        return CURRENT;
    }

    private boolean isDuplicate(int id) {
        for (World world : Core.getWorlds()) {
            // TODO: Make this a filter
            for (Entity entity : world.getEntities()) {
                if (entity.getEntityId() == id) return true;
            }
        }
        return false;
    }
}

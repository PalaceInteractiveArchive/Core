package network.palace.core.npc;

import lombok.Getter;
import network.palace.core.Core;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;

public class EntityIDManager {

    @Getter private final Set<WeakReference<AbstractMob>> mobRefs = new HashSet<>();

    private int CURRENT = 2000;

    public void ensureAllValid() {
        mobRefs.removeIf(mob -> mob.get() == null);
    }

    public int getNextID() {
        do {
            CURRENT++;
        } while (isDuplicate(CURRENT));
        return CURRENT;
    }

    private boolean isDuplicate(int id) {
        for (World world : Core.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity.getEntityId() == id) return true;
            }
        }
        return false;
    }
}

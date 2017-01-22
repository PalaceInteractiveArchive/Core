package network.palace.core.pathfinding.npc;

import network.palace.core.pathfinding.Point;
import network.palace.core.player.CPlayer;

import java.lang.reflect.Constructor;
import java.util.Set;

public final class NPCFactory {

    public <T extends AbstractMob> T createNPC(Class<T> type, Point point, Set<CPlayer> viewers, String title) {
        try {
            Constructor<T> constructor = type.getDeclaredConstructor(Point.class, Set.class, String.class);
            return constructor.newInstance(point, viewers, title);
        } catch (Exception e) {
            throw new RuntimeException("Unable to create mob ", e);
        }
    }
}

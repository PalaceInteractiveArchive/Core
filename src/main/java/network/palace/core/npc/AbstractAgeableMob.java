package network.palace.core.npc;

import lombok.Setter;
import network.palace.core.pathfinding.Point;
import network.palace.core.player.CPlayer;

import java.util.Set;

public abstract class AbstractAgeableMob extends AbstractMob {

    @Setter private boolean baby = false;

    public AbstractAgeableMob(Point location, Set<CPlayer> observers, String title) {
        super(location, observers, title);
    }

    @Override
    protected void onDataWatcherUpdate() {
        int babyIndex = 12;
        getDataWatcher().setObject(ProtocolLibSerializers.getBoolean(babyIndex), baby);
        super.onDataWatcherUpdate();
    }
}

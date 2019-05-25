package network.palace.core.npc.mob;

import network.palace.core.npc.AbstractAmbient;
import network.palace.core.npc.ProtocolLibSerializers;
import network.palace.core.pathfinding.Point;
import network.palace.core.player.CPlayer;
import org.bukkit.entity.EntityType;

import java.util.Set;

public class MobBat extends AbstractAmbient {
    private boolean awake = true;

    public MobBat(Point location, Set<CPlayer> observers, String title) {
        super(location, observers, title);
    }

    @Override
    protected EntityType getEntityType() {
        return EntityType.BAT;
    }

    @Override
    public float getMaximumHealth() {
        return 6f;
    }

    @Override
    protected void onDataWatcherUpdate() {
        getDataWatcher().setObject(ProtocolLibSerializers.getBoolean(13), awake);
        super.onDataWatcherUpdate();
    }

    public void setAwake(boolean b) {
        this.awake = b;
        getDataWatcher().setObject(ProtocolLibSerializers.getBoolean(13), b);
        updateDataWatcher();
    }

    public boolean isAwake() {
        return awake;
    }
}

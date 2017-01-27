package network.palace.core.npc;

import network.palace.core.pathfinding.Point;
import network.palace.core.player.CPlayer;
import org.bukkit.World;

import java.util.Set;

public abstract class AbstractTameableMob extends AbstractAgeableMob {

    private boolean tame = false;
    private boolean sitting = false;
    private String ownerName = "Notch";

    public AbstractTameableMob(Point location, World world, Set<CPlayer> observers, String title) {
        super(location, world, observers, title);
    }

    @Override
    protected void onDataWatcherUpdate() {
        int metadataIndex = 13;
        byte value = 0;
        if (sitting) value |= 0x01;
        if (tame) value |= 0x04;
        getDataWatcher().setObject(ProtocolLibSerializers.getByte(metadataIndex), value);
        // TODO Fix
        //if (ownerName == null) ownerName = "Notch";
        //getDataWatcher().setObject(14, ownerName);
        super.onDataWatcherUpdate();
    }

    public void playHeartParticles() {
        playStatus(6);
    }

    public void playSmokeParticles() {
        playStatus(7);
    }

    public void playHeartParticles(Set<CPlayer> players) {
        playStatus(players, 6);
    }

    public void playSmokeParticles(Set<CPlayer> players) {
        playStatus(players, 7);
    }
}

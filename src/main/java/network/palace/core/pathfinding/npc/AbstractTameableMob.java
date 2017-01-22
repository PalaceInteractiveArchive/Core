package network.palace.core.pathfinding.npc;

import lombok.NonNull;
import network.palace.core.pathfinding.Point;
import network.palace.core.player.CPlayer;
import org.bukkit.World;

import java.util.Set;

/**
 * @author Innectic
 * @since 1/21/2017
 */
public abstract class AbstractTameableMob extends AbstractAgeableMob {
    private boolean tame = false;
    private boolean sitting = false;
    @NonNull
    private String ownerName = "Notch";

    public AbstractTameableMob(Point location, World world, Set<CPlayer> observers, String title) {
        super(location, world, observers, title);
    }

    @Override
    protected void onDataWatcherUpdate() {
        super.onDataWatcherUpdate();
        byte value = 0;
        if (sitting) value |= 0x01;
        if (tame) value |= 0x04;
        getDataWatcher().setObject(16, value);
        if (ownerName != null) getDataWatcher().setObject(17, ownerName);
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
package network.palace.core.player.impl.managers;

import lombok.AllArgsConstructor;
import network.palace.core.player.CPlayer;
import network.palace.core.player.CPlayerParticlesManager;
import network.palace.core.player.PlayerStatus;
import org.bukkit.Location;
import org.bukkit.Particle;

/**
 * The type Core player particles manager.
 */
@AllArgsConstructor
public class CorePlayerParticlesManager implements CPlayerParticlesManager {

    private final CPlayer player;

    @Override
    public void send(Particle particle) {
        send(particle, 1);
    }

    @Override
    public void send(Particle particle, int count) {
        send(player.getLocation(), particle, count);
    }

    @Override
    public void send(Location location, Particle particle) {
        send(location, particle, 1);
    }

    @Override
    public void send(Location location, Particle particle, int count) {
        send(location, particle, count, 0, 0, 0, 0);
    }

    @Override
    public void send(Location location, Particle particle, int count, float offsetX, float offsetY, float offsetZ, float extra) {
        if (player.getStatus() != PlayerStatus.JOINED) return;
        if (player.getBukkitPlayer() == null) return;
        player.getBukkitPlayer().spawnParticle(particle, location, count, offsetX, offsetY, offsetZ, extra);
    }
}

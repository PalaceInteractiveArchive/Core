package network.palace.core.player;

import org.bukkit.Location;
import org.bukkit.Particle;

/**
 * The interface C player particles manager.
 */
public interface CPlayerParticlesManager {

    /**
     * Spawns one particle at the player location.
     *
     * @param particle the particle to spawn
     */
    void send(Particle particle);

    /**
     * Spawns the particle (the number of times specified by count) at the player location.
     *
     * @param particle the particle to spawn
     * @param count the number of particles
     */
    void send(Particle particle, int count);

    /**
     * Spawns one particle (the number of times specified by count) at the target location
     *
     * @param location the location to spawn at
     * @param particle the particle to spawn
     */
    void send(Location location, Particle particle);

    /**
     * Spawns the particle (the number of times specified by count) at the target location.
     *
     * @param location the location to spawn at
     * @param particle the particle to spawn
     * @param count the number of particles
     */
    void send(Location location, Particle particle, int count);

    /**
     * Spawns the particle (the number of times specified by count)
     * at the target location. The position of each particle will be
     * randomized positively and negatively by the offset parameters
     * on each axis.
     *
     * @param location the location to spawn at
     * @param particle the particle to spawn
     * @param count the number of particles
     * @param offsetX the maximum random offset on the X axis
     * @param offsetY the maximum random offset on the Y axis
     * @param offsetZ the maximum random offset on the Z axis
     * @param extra the extra data for this particle, depends on the particle used (normally speed)
     */
    void send(Location location, Particle particle, int count, float offsetX, float offsetY, float offsetZ, float extra);
}

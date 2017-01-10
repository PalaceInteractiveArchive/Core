package network.palace.core.player;

import com.comphenix.protocol.wrappers.EnumWrappers;
import org.bukkit.Location;

/**
 * The interface C player particles manager.
 */
public interface CPlayerParticlesManager {
    /**
     * Send particle.
     *
     * @param particle the particle
     */
    void send(EnumWrappers.Particle particle);

    /**
     * Send particle.
     *
     * @param particle          the particle
     * @param numberOfParticles the number of particles
     */
    void send(EnumWrappers.Particle particle, int numberOfParticles);

    /**
     * Send particle.
     *
     * @param location the location
     * @param particle the particle
     */
    void send(Location location, EnumWrappers.Particle particle);

    /**
     * Send particle.
     *
     * @param location          the location
     * @param particle          the particle
     * @param numberOfParticles the number of particles
     */
    void send(Location location, EnumWrappers.Particle particle, int numberOfParticles);

    /**
     * Send particle.
     *
     * @param location          the location
     * @param particle          the particle
     * @param numberOfParticles the number of particles
     * @param offsetX           the offset x
     * @param offsetY           the offset y
     * @param offsetZ           the offset z
     * @param speed             the speed
     */
    void send(Location location, EnumWrappers.Particle particle, int numberOfParticles, float offsetX, float offsetY, float offsetZ, float speed);
}

package com.thepalace.core.player;

import com.comphenix.protocol.wrappers.EnumWrappers;
import org.bukkit.Location;

@SuppressWarnings("unused")
public interface CPlayerParticlesManager {

    void send(EnumWrappers.Particle particle);
    void send(EnumWrappers.Particle particle, int numberOfParticles);
    void send(Location location, EnumWrappers.Particle particle);
    void send(Location location, EnumWrappers.Particle particle, int numberOfParticles);

}

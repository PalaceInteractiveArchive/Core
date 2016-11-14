package com.palacemc.core.player.impl;

import com.palacemc.core.packets.server.WrapperPlayServerWorldParticles;
import com.palacemc.core.player.CPlayer;
import com.palacemc.core.player.CPlayerParticlesManager;
import com.comphenix.protocol.wrappers.EnumWrappers;
import org.bukkit.Location;

public class CorePlayerParticlesManager implements CPlayerParticlesManager {

    private CPlayer player;

    public CorePlayerParticlesManager(CPlayer player) {
        this.player = player;
    }

    @Override
    public void send(EnumWrappers.Particle particle) {
        send(particle, 1);
    }

    @Override
    public void send(EnumWrappers.Particle particle, int numberOfParticles) {
        send(player.getLocation(), particle, numberOfParticles);
    }

    @Override
    public void send(Location location, EnumWrappers.Particle particle) {
        send(location, particle, 1);
    }

    @Override
    public void send(Location location, EnumWrappers.Particle particle, int numberOfParticles) {
        WrapperPlayServerWorldParticles packet = new WrapperPlayServerWorldParticles();
        packet.setParticleType(particle);
        packet.setLongDistance(true);
        packet.setLocation(location);
        packet.setNumberOfParticles(numberOfParticles);
        player.sendPacket(packet);
    }
}

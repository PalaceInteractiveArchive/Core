package network.palace.core.player.impl;

import com.comphenix.protocol.wrappers.EnumWrappers;
import lombok.AllArgsConstructor;
import network.palace.core.packets.server.WrapperPlayServerWorldParticles;
import network.palace.core.player.CPlayer;
import network.palace.core.player.CPlayerParticlesManager;
import org.bukkit.Location;

@AllArgsConstructor
public class CorePlayerParticlesManager implements CPlayerParticlesManager {

    private final CPlayer player;

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
        send(location, particle, numberOfParticles, 0, 0, 0, 0);
    }

    @Override
    public void send(Location location, EnumWrappers.Particle particle, int numberOfParticles, float offsetX, float offsetY, float offsetZ, float speed) {
        WrapperPlayServerWorldParticles packet = new WrapperPlayServerWorldParticles();
        packet.setParticleType(particle);
        packet.setLongDistance(true);
        packet.setLocation(location);
        packet.setNumberOfParticles(numberOfParticles);
        packet.setOffsetX(offsetX);
        packet.setOffsetY(offsetY);
        packet.setOffsetZ(offsetZ);
        packet.setParticleData(speed);
        player.sendPacket(packet);
    }
}

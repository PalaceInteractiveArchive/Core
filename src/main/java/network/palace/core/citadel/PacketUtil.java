package network.palace.core.citadel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import network.palace.core.Core;
import network.palace.core.citadel.packets.outgoing.PacketOutgoingBase;
import org.bukkit.ChatColor;

/**
 * @author Innectic
 * @since 2/12/2017
 */
public class PacketUtil {

    @Getter private static ObjectMapper mapper = new ObjectMapper();

    public String toJson(PacketOutgoingBase packet) {
        try {
            return mapper.writeValueAsString(packet);
        } catch (JsonProcessingException e) {
            Core.logMessage("Core", ChatColor.DARK_RED + "Error converting packet to json for " + packet.getClass().getSimpleName());
            return "";
        }
    }
}
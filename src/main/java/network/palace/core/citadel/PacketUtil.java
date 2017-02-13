package network.palace.core.citadel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import network.palace.core.Core;
import network.palace.core.citadel.packets.PacketBase;
import org.bukkit.ChatColor;

/**
 * @author Innectic
 * @since 2/12/2017
 */
public class PacketUtil {

    private static ObjectMapper mapper = new ObjectMapper();

    public String toJson(PacketBase packet) {
        try {
            return mapper.writeValueAsString(packet);
        } catch (JsonProcessingException e) {
            Core.logMessage("Core", ChatColor.DARK_RED + "Error converting packet to json for " + packet.getClass().getSimpleName());
            return "";
        }
    }
}
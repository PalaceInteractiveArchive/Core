package network.palace.core.utils;

import com.comphenix.protocol.ProtocolLibrary;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ProtocolUtil {

    public static int getProtocolVersion(Player player) {
        if (Bukkit.getPluginManager().getPlugin("ViaVersion") != null) {
            us.myles.ViaVersion.api.ViaAPI api = us.myles.ViaVersion.api.Via.getAPI();
            return api.getPlayerVersion(player);
        } else {
            return ProtocolLibrary.getProtocolManager().getProtocolVersion(player);
        }
    }
}

package network.palace.core.citadel.packets.incoming;

import com.fasterxml.jackson.annotation.JsonProperty;
import network.palace.core.Core;
import org.bukkit.ChatColor;

/**
 * @author Innectic
 * @since 2/13/2017
 */
public class PacketAuthenticatedData {

    private boolean authenticated = false;
    private String server = "";

    public PacketAuthenticatedData(@JsonProperty("authenticated") boolean authenticated, @JsonProperty("server")  String server) {
        this.authenticated = authenticated;
        this.server = server;
        if (authenticated) {
            Core.logMessage("Citadel", ChatColor.DARK_GREEN + "Authenticated for " + server + "!");
        } else {
            Core.logMessage("Citadel", ChatColor.DARK_RED + "Could not authenticated for " + server + "!");
            if (!Core.isCitadelAndSqlDisabled()) {
                // TODO SOMETHING
            }
        }
    }
}

package network.palace.core.citadel;

import network.palace.core.Core;
import org.bukkit.ChatColor;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_10;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author Innectic
 * @since 2/12/2017
 */
public class Citadel {

    private WebSocketClient client;
    private final String citadelURI;

    public Citadel(String uri) {
        this.client = null;
        this.citadelURI = uri;
    }

    public void connect() {
        if (citadelURI == "") {
            Core.logMessage("Citadel", ChatColor.DARK_RED + "Provide a url to connect to Citadel with");
            return;
        }

        if (client != null) {
            client.close();
            client = null;
        }
        try {
            client = new WebSocketClient(new URI(citadelURI), new Draft_10()) {
                @Override
                public void onOpen(ServerHandshake serverHandshake) {

                }

                @Override
                public void onMessage(String s) {

                }

                @Override
                public void onClose(int i, String s, boolean b) {

                }

                @Override
                public void onError(Exception e) {
                    Core.logMessage("Citadel", ChatColor.DARK_RED + e.toString());
                }
            };
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}

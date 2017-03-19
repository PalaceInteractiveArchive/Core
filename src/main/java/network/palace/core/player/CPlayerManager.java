package network.palace.core.player;

import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * The interface C player manager.
 */
public interface CPlayerManager {

    /**
     * Player logged in.
     *
     * @param uuid the uuid
     * @param name the name
     */
    void playerLoggedIn(UUID uuid, String name);

    /**
     * Player joined.
     *
     * @param uuid        the uuid
     * @param textureHash the texture hash
     */
    void playerJoined(UUID uuid, String textureHash);

    /**
     * Player logged out.
     *
     * @param uuid the uuid
     */
    void playerLoggedOut(UUID uuid);

    /**
     * Broadcast message to all players.
     *
     * @param message the message
     */
    void broadcastMessage(String message);

    /**
     * Gets player.
     *
     * @param playerUUID the player uuid
     * @return the player
     */
    CPlayer getPlayer(UUID playerUUID);

    /**
     * Gets player.
     *
     * @param player the player
     * @return the player
     */
    CPlayer getPlayer(Player player);

    /**
     * Gets online players.
     *
     * @return the online players
     */
    List<CPlayer> getOnlinePlayers();
}

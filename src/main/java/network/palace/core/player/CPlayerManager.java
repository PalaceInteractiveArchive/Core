package network.palace.core.player;

import org.bukkit.entity.Player;

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
     * @param player the player
     */
    void playerJoined(Player player);

    /**
     * Player logged out.
     *
     * @param player the player
     */
    void playerLoggedOut(Player player);

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
     * Gets player.
     *
     * @param name the player name
     * @return the player
     */
    CPlayer getPlayer(String name);

    /**
     * Gets online players.
     *
     * @return the online players
     */
    List<CPlayer> getOnlinePlayers();
}

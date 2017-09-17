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
     * @param uuid the uuid of the player
     * @param name the name of the player
     */
    void playerLoggedIn(UUID uuid, String name);

    /**
     * Player joined.
     *
     * @param player the player who joined
     */
    void playerJoined(Player player);

    /**
     * Player logged out.
     *
     * @param player the player who logged out
     */
    void playerLoggedOut(Player player);

    /**
     * Remove player object if it exists
     *
     * @param uuid the uuid of the player
     */
    void removePlayer(UUID uuid);

    /**
     * Broadcast message to all players.
     *
     * @param message the message to broadcast
     */
    void broadcastMessage(String message);

    /**
     * Get a player by UUID
     *
     * @param playerUUID the player uuid
     * @return the player
     */
    CPlayer getPlayer(UUID playerUUID);

    /**
     * Get a CorePlayer from a bukkit player
     *
     * @param player the bukkit player
     * @return the core player
     */
    CPlayer getPlayer(Player player);

    /**
     * Get a player by name
     *
     * @param name the player name
     * @return the player
     */
    CPlayer getPlayer(String name);

    /**
     * Get a player by sql id
     *
     * @param sqlId the sql id
     * @return the player
     */
    CPlayer getPlayer(int sqlId);

    /**
     * Gets online players.
     *
     * @return the online players
     */
    List<CPlayer> getOnlinePlayers();

    /**
     * Display the rank to all players.
     *
     * @param player the player who's rank to display
     */
    void displayRank(CPlayer player);
}

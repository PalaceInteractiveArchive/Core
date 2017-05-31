package network.palace.core.player;

import java.util.List;

/**
 * The interface C player achievement manager
 */
public interface CPlayerAchievementManager {

    /**
     * Get a list of the player's achievements
     *
     * @return the player's achievement IDs
     */
    List<Integer> getAchievements();

    /**
     * Check if player already has a CoreAchievement
     * @param id ID of achievement to check
     * @return if player has achievement
     */
    boolean hasAchievement(int id);

    /**
     * Give player a CoreAchievement
     * Method checks if player has achievement already
     *
     * @param id ID of achievement to give to player
     */
    void giveAchievement(int id);
}

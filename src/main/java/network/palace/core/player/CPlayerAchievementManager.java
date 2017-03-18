package network.palace.core.player;

import java.util.List;

/**
 * The interface C player achievement manager
 */
public interface CPlayerAchievementManager {

    /**
     * Get a list of the player's CoreAchievement
     *
     * @return Integer list of CoreAchievement IDs
     */
    List<Integer> getAchievements();

    /**
     * Check if player already has a CoreAchievement
     * @param i ID of CoreAchievement to check
     * @return if player has achievement
     */
    boolean hasAchievement(int i);

    /**
     * Give player a CoreAchievement
     * Method checks if player has achievement already
     *
     * @param i ID of CoreAchievement to give to player
     */
    void giveAchievement(int i);
}

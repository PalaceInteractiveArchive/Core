package network.palace.core.player;

/**
 * The interface C player scoreboard manager.
 */
public interface CPlayerScoreboardManager {

    /**
     * Set scoreboard text for a id
     *
     * @param id   the id to set
     * @param text the text that should be displayed
     * @return the manager instance
     */
    CPlayerScoreboardManager set(int id, String text);

    /**
     * Set scoreboard text to blank for a id
     *
     * @param id the id to set blank
     * @return the manager instance
     */
    CPlayerScoreboardManager setBlank(int id);

    /**
     * Remove scoreboard text to blank for a id
     *
     * @param id the id to remove
     * @return the c player scoreboard manager
     */
    CPlayerScoreboardManager remove(int id);

    /**
     * Set the title of the scoreboard
     *
     * @param title the new title of the scoreboard
     * @return the manager instance
     */
    CPlayerScoreboardManager title(String title);

    /**
     * Set the rank tags
     */
    void setupPlayerTags();

    /**
     * Add player tag.
     *
     * @param otherPlayer the other player
     */
    void addPlayerTag(CPlayer otherPlayer);

    /**
     * Remove player tag.
     *
     * @param otherPlayer the other player
     */
    void removePlayerTag(CPlayer otherPlayer);

    /**
     * Is scoreboard setup.
     *
     * @return the boolean
     */
    boolean isSetup();

    /**
     * Clear the scoreboard
     */
    void clear();

    /**
     * Hide username tags
     */
    void toggleTags();

    /**
     * Hide username tags
     *
     * @param hidden whether or not tags should be hidden
     */
    void toggleTags(boolean hidden);

    /**
     * Get whether tags are visible
     *
     * @return whether tags are visible
     */
    boolean getTagsVisible();
}

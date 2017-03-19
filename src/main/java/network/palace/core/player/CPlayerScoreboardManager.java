package network.palace.core.player;

/**
 * The interface C player scoreboard manager.
 */
public interface CPlayerScoreboardManager {
    /**
     * Set scoreboard text for a id
     *
     * @param id   the id
     * @param text the text
     * @return the c player scoreboard manager
     */
    CPlayerScoreboardManager set(int id, String text);

    /**
     * Set scoreboard text to blank for a id
     *
     * @param id the id
     * @return the blank
     */
    CPlayerScoreboardManager setBlank(int id);

    /**
     * Remove scoreboard text to blank for a id
     *
     * @param id the id
     * @return the c player scoreboard manager
     */
    CPlayerScoreboardManager remove(int id);

    /**
     * Set scoreboard title
     *
     * @param title the title
     * @return the c player scoreboard manager
     */
    CPlayerScoreboardManager title(String title);

    /**
     * Sets player tags.
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
}

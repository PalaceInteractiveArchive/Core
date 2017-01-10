package network.palace.core.player;

/**
 * The interface C player title manager.
 */
public interface CPlayerTitleManager {
    /**
     * Show title
     *
     * @param title    the title
     * @param subtitle the subtitle
     */
    void show(String title, String subtitle);

    /**
     * Show title
     *
     * @param title    the title
     * @param subtitle the subtitle
     * @param fadeIn   the fade in
     * @param stay     the stay
     * @param fadeOut  the fade out
     */
    void show(String title, String subtitle, int fadeIn, int stay, int fadeOut);

    /**
     * Hide title
     */
    void hide();
}

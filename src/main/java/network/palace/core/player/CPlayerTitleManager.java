package network.palace.core.player;

/**
 * The interface C player title manager.
 */
public interface CPlayerTitleManager {

    /**
     * Sends a title message to the player. If title value is null,
     * it will not be sent and the display will remain
     * unchanged. If title is a empty string, the display will be updated as
     * such. If the string contains a new line, only the first line will be
     * sent. The titles will be displayed with the client's default timings.
     *
     * @param title Title text
     */
    void show(String title);

    /**
     * Sends a title and a subtitle message to the player. If either of these
     * values are null, they will not be sent and the display will remain
     * unchanged. If they are empty strings, the display will be updated as
     * such. If the strings contain a new line, only the first line will be
     * sent. The titles will be displayed with the client's default timings.
     *
     * @param title Title text
     * @param subtitle Subtitle text
     */
    void show(String title, String subtitle);

    /**
     * Shows a title and a subtitle message to the player. If either of these
     * values are null, they will not be sent and the display will remain
     * unchanged. If they are empty strings, the display will be updated as
     * such. If the strings contain a new line, only the first line will be
     * sent. All timings values may take a value of -1 to indicate that they
     * will use the last value sent (or the defaults if no title has been
     * displayed).
     *
     * @param title Title text
     * @param subtitle Subtitle text
     * @param fadeIn time in ticks for titles to fade in. Defaults to 10.
     * @param stay time in ticks for titles to stay. Defaults to 70.
     * @param fadeOut time in ticks for titles to fade out. Defaults to 20.
     */
    void show(String title, String subtitle, int fadeIn, int stay, int fadeOut);

    /**
     * Resets the title displayed to the player. This will clear the displayed
     * title / subtitle and reset timings to their default values.
     */
    void hide();
}

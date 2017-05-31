package network.palace.core.player;

import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;

/**
 * The interface C player boss bar manager.
 */
public interface CPlayerBossBarManager {

    /**
     * Sets title.
     *
     * @param title the new title
     */
    void setText(String title);

    /**
     * Sets progress.
     *
     * @param progress the new progress
     */
    void setProgress(double progress);

    /**
     * Sets color.
     *
     * @param color the new color
     */
    void setColor(BarColor color);

    /**
     * Sets style.
     *
     * @param style the new style
     */
    void setStyle(BarStyle style);

    /**
     * Sets text and progress.
     *
     * @param title    the new title
     * @param progress the new progress
     */
    void setTextAndProgress(String title, double progress);

    /**
     * Sets everything.
     *
     * @param title    the new title
     * @param progress the new progress
     * @param color    the new color
     * @param style    the new style
     */
    void setEverything(String title, double progress, BarColor color, BarStyle style);

    /**
     * Show the boss bar.
     */
    void show();

    /**
     * Hide the boss bar.
     */
    void hide();

    /**
     * Remove the boss bar.
     */
    void remove();
}

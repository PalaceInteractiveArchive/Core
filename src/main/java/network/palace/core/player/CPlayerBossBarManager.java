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
     * @param title the title
     */
    void setText(String title);

    /**
     * Sets progress.
     *
     * @param progress the progress
     */
    void setProgress(double progress);

    /**
     * Sets color.
     *
     * @param color the color
     */
    void setColor(BarColor color);

    /**
     * Sets style.
     *
     * @param style the style
     */
    void setStyle(BarStyle style);

    /**
     * Sets text and progress.
     *
     * @param title    the title
     * @param progress the progress
     */
    void setTextAndProgress(String title, double progress);

    /**
     * Sets everything.
     *
     * @param title    the title
     * @param progress the progress
     * @param color    the color
     * @param style    the style
     */
    void setEverything(String title, double progress, BarColor color, BarStyle style);

    /**
     * Show boss bar.
     */
    void show();

    /**
     * Hide boss bar.
     */
    void hide();

    /**
     * Remove boss bar.
     */
    void remove();
}

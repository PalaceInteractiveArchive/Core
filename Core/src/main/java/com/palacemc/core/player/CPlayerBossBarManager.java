package com.palacemc.core.player;

import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;

public interface CPlayerBossBarManager {

    void setText(String title);
    void setProgress(double progress);
    void setColor(BarColor color);
    void setStyle(BarStyle style);
    void setTextAndProgress(String title, double progress);
    void setEverything(String title, double progress, BarColor color, BarStyle style);

    void show();
    void hide();
    void remove();

}

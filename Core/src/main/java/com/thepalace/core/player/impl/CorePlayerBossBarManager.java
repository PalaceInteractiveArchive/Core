package com.thepalace.core.player.impl;

import com.thepalace.core.player.CPlayer;
import com.thepalace.core.player.CPlayerBossBarManager;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;

public class CorePlayerBossBarManager implements CPlayerBossBarManager {

    private final CPlayer player;
    private BossBar bossBar = null;

    public CorePlayerBossBarManager(CPlayer player) {
        this.player = player;
    }

    @Override
    public void setText(String title) {
        BossBar bossBar = createIfDoesNotExist();
        bossBar.setTitle(title);
        show();
    }

    @Override
    public void setProgress(double progress) {
        BossBar bossBar = createIfDoesNotExist();
        if (progress < 0.0) progress = 0.0;
        if (progress > 1.0) progress = 1.0;
        bossBar.setProgress(progress);
        show();
    }

    @Override
    public void setColor(BarColor color) {
        BossBar bossBar = createIfDoesNotExist();
        bossBar.setColor(color);
        show();
    }

    @Override
    public void setStyle(BarStyle style) {
        BossBar bossBar = createIfDoesNotExist();
        bossBar.setStyle(style);
        show();
    }

    @Override
    public void setTextAndProgress(String text, double progress) {
        setText(text);
        setProgress(progress);
        show();
    }

    @Override
    public void setEverything(String text, double progress, BarColor color, BarStyle style) {
        setTextAndProgress(text, progress);
        setColor(color);
        setStyle(style);
        show();
    }

    @Override
    public void show() {
        BossBar bossBar = createIfDoesNotExist();
        if (bossBar == null) return;
        bossBar.setVisible(true);
    }

    @Override
    public void hide() {
        if (bossBar == null) return;
        bossBar.setVisible(false);
    }

    @Override
    public void remove() {
        if (bossBar == null) return;
        hide();
        bossBar.removeAll();
        this.bossBar = null;
    }

    private BossBar createIfDoesNotExist() {
        if (bossBar != null) {
            return bossBar;
        }
        BossBar bossBar = Bukkit.createBossBar("Boss Bar", BarColor.PINK, BarStyle.SOLID);
        bossBar.addPlayer(player.getBukkitPlayer());
        this.bossBar = bossBar;
        setProgress(1.0);
        show();
        return bossBar;
    }
}

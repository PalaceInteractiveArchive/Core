package com.palacemc.core.player;

public interface CPlayerTitleManager {

    void show(String title, String subtitle);
    void show(String title, String subtitle, int fadeIn, int stay, int fadeOut);
    void hide();

}

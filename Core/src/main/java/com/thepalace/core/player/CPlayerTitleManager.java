package com.thepalace.core.player;

@SuppressWarnings("unused")
public interface CPlayerTitleManager {

    void show(String title, String subtitle);
    void show(String title, String subtitle, int fadeIn, int stay, int fadeOut);
    void hide();

}

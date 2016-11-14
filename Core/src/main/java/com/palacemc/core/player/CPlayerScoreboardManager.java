package com.palacemc.core.player;

public interface CPlayerScoreboardManager {

    String getTitle();
    void setTitle(String title);

    String getLine(int line);
    void setLine(int line, String info);

    void setToSize(int size);
    void reset();

}

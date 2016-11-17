package com.thepalace.core.player;

public interface CPlayerScoreboardManager {

    String getTitle();
    void setTitle(String title);

    String getLine(int line);
    void setLine(int line, String info);

    void setToSize(int size);

    void setScore(String line, int score);

    void reset();

}

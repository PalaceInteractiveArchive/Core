package com.palacemc.core.player;

public interface CPlayerResourcePackManager {

    void send(String url);
    void send(String url, String hash);

}

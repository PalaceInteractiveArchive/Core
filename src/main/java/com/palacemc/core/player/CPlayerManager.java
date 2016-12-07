package com.palacemc.core.player;

import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.UUID;

@SuppressWarnings("unused")
public interface CPlayerManager extends Iterable<CPlayer> {

    void playerLoggedIn(UUID uuid, String name);
    void playerJoined(UUID uuid);
    void playerLoggedOut(UUID uuid);

    void broadcastMessage(String message);

    CPlayer getPlayer(UUID playerUUID);
    CPlayer getPlayer(Player player);

    Collection<CPlayer> getOnlinePlayers();
}

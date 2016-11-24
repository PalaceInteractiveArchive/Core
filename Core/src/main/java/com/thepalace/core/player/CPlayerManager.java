package com.thepalace.core.player;

import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.UUID;

@SuppressWarnings("unused")
public interface CPlayerManager extends Iterable<CPlayer> {

    void playerLoggedIn(Player player);
    void playerLoggedOut(Player player);

    void broadcastMessage(String message);

    CPlayer getPlayer(Player player);
    CPlayer getPlayer(UUID uuid);

    Collection<CPlayer> getOnlinePlayers();
}

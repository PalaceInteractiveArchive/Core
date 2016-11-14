package com.palacemc.core.player;

import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.UUID;

public interface CPlayerManager extends Iterable<CPlayer> {

    void playerLoggedIn(Player player);
    void playerLoggedOut(Player player);

    CPlayer getPlayer(Player player);
    CPlayer getPlayer(UUID uuid);

    Collection<CPlayer> getOnlinePlayers();
}

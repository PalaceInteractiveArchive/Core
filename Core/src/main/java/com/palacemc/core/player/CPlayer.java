package com.palacemc.core.player;

import com.palacemc.core.packets.AbstractPacket;
import com.palacemc.core.plugin.Plugin;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public interface CPlayer {

    String getName();
    boolean isOnline();

    GameMode getGamemode();
    void setGamemode(GameMode gamemode);
    Location getLocation();
    void setLocation(Location location);

    void sendMessage(String message);
    void sendFormatMessage(JavaPlugin plugin, String key);

    void resetPlayer();
    void resetManagers();

    CPlayerActionBarManager getActionBar();
    CPlayerBossBarManager getBossBar();
    CPlayerHeaderFooterManager getHeaderFooter();
    CPlayerResourcePackManager getResourcePack();
    CPlayerScoreboardManager getScoreboard();
    CPlayerTitleManager getTitle();

    void sendPacket(AbstractPacket packet);

    void setLocale(String locale);
    String getLocale();

    Player getBukkitPlayer();
    UUID getUuid();

}

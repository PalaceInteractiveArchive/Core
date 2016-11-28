package com.thepalace.core.player;

import com.thepalace.core.packets.AbstractPacket;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

@SuppressWarnings("unused")
public interface CPlayer {

    String getName();
    boolean isOnline();

    void setMaxHealth(double health);
    void setHealth(double health);

    GameMode getGamemode();
    void setGamemode(GameMode gamemode);
    Location getLocation();
    void teleport(Location location);

    void sendMessage(String message);
    void sendFormatMessage(JavaPlugin plugin, String key);

    void resetPlayer();
    void resetManagers();

    void setInventorySlot(int slot, ItemStack stack);
    PlayerInventory getInventory();
    void openInventory(Inventory inventory);
    void closeInventory();

    void respawn();

    CPlayerActionBarManager getActionBar();
    CPlayerBossBarManager getBossBar();
    CPlayerHeaderFooterManager getHeaderFooter();
    CPlayerResourcePackManager getResourcePack();
    CPlayerScoreboardManager getScoreboard();
    CPlayerTitleManager getTitle();

    void sendPacket(AbstractPacket packet);
    void sendToServer(String server);

    void setLocale(String locale);
    String getLocale();

    Player getBukkitPlayer();
    UUID getUuid();

    void setStatus(PlayerStatus status);
    PlayerStatus getStatus();

    enum PlayerStatus {
        LOGIN, JOINED, LEFT
    }
}

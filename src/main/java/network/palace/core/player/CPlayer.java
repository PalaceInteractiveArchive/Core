package network.palace.core.player;

import network.palace.core.packets.AbstractPacket;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
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

    void setDisplayName(String name);

    PlayerInventory getInventory();
    void openInventory(Inventory inventory);
    void closeInventory();
    void setInventorySlot(int slot, ItemStack stack);
    void addToInventory(ItemStack... itemStacks);
    boolean doesInventoryContain(Material material);
    void removeFromInventory(Material material);

    ItemStack getHelmet();
    void setHelmet(ItemStack itemStack);
    ItemStack getChestplate();
    void setChestplate(ItemStack itemStack);
    ItemStack getLeggings();
    void setLeggings(ItemStack itemStack);
    ItemStack getBoots();
    void setBoots(ItemStack itemStack);

    ItemStack getMainHand();
    void setMainHand(ItemStack itemStack);
    ItemStack getOffHand();
    void setOffHand(ItemStack itemStack);


    void respawn();
    void showPlayer(CPlayer player);
    void hidePlayer(CPlayer player);

    CPlayerActionBarManager getActionBar();
    CPlayerBossBarManager getBossBar();
    CPlayerHeaderFooterManager getHeaderFooter();
    CPlayerParticlesManager getParticles();
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

    Rank getRank();
    void setRank(Rank rank);

    void setTextureHash(String textureHash);
    String getTextureHash();

    enum PlayerStatus {
        LOGIN, JOINED, LEFT
    }
}

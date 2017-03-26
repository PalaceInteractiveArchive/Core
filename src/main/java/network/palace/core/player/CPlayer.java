package network.palace.core.player;

import network.palace.core.packets.AbstractPacket;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

/**
 * The interface C player.
 */
public interface CPlayer {

    /**
     * Gets player name.
     *
     * @return player name
     */
    String getName();

    /**
     * Is online boolean.
     *
     * @return the boolean
     */
    boolean isOnline();

    /**
     * Sets op.
     *
     * @param isOp the is op
     */
    void setOp(boolean isOp);

    /**
     * Is op boolean.
     *
     * @return the boolean
     */
    boolean isOp();

    /**
     * Play sound.
     *
     * @param location the location
     * @param sound    the sound
     * @param volume   the volume
     * @param pitch    the pitch
     */
    void playSound(Location location, Sound sound, float volume, float pitch);

    /**
     * Sets max health.
     *
     * @param health the health
     */
    void setMaxHealth(double health);

    /**
     * Sets health.
     *
     * @param health the health
     */
    void setHealth(double health);

    /**
     * Gets gamemode.
     *
     * @return the gamemode
     */
    GameMode getGamemode();

    /**
     * Sets gamemode.
     *
     * @param gamemode the gamemode
     */
    void setGamemode(GameMode gamemode);

    /**
     * Gets location.
     *
     * @return the location
     */
    Location getLocation();

    /**
     * Teleport.
     *
     * @param location the location
     */
    void teleport(Location location);

    /**
     * Send message.
     *
     * @param message the message
     */
    void sendMessage(String message);

    /**
     * Send format message.
     *
     * @param plugin the plugin
     * @param key    the key
     */
    void sendFormatMessage(JavaPlugin plugin, String key);

    /**
     * Reset player.
     */
    void resetPlayer();

    /**
     * Reset managers.
     */
    void resetManagers();

    /**
     * Sets display name.
     *
     * @param name the name
     */
    void setDisplayName(String name);

    /**
     * Gets inventory.
     *
     * @return the inventory
     */
    PlayerInventory getInventory();

    /**
     * Open inventory.
     *
     * @param inventory the inventory
     */
    void openInventory(Inventory inventory);

    /**
     * Close inventory.
     */
    void closeInventory();

    /**
     * Sets inventory slot.
     *
     * @param slot  the slot
     * @param stack the stack
     */
    void setInventorySlot(int slot, ItemStack stack);

    /**
     * Add to inventory.
     *
     * @param itemStacks the item stacks
     */
    void addToInventory(ItemStack... itemStacks);

    /**
     * Does inventory contain boolean.
     *
     * @param material the material
     * @return the boolean
     */
    boolean doesInventoryContain(Material material);

    /**
     * Remove from inventory.
     *
     * @param material the material
     */
    void removeFromInventory(Material material);

    /**
     * Gets helmet.
     *
     * @return the helmet
     */
    ItemStack getHelmet();

    /**
     * Sets helmet.
     *
     * @param itemStack the item stack
     */
    void setHelmet(ItemStack itemStack);

    /**
     * Gets chestplate.
     *
     * @return the chestplate
     */
    ItemStack getChestplate();

    /**
     * Sets chestplate.
     *
     * @param itemStack the item stack
     */
    void setChestplate(ItemStack itemStack);

    /**
     * Gets leggings.
     *
     * @return the leggings
     */
    ItemStack getLeggings();

    /**
     * Sets leggings.
     *
     * @param itemStack the item stack
     */
    void setLeggings(ItemStack itemStack);

    /**
     * Gets boots.
     *
     * @return the boots
     */
    ItemStack getBoots();

    /**
     * Sets boots.
     *
     * @param itemStack the item stack
     */
    void setBoots(ItemStack itemStack);

    /**
     * Gets main hand.
     *
     * @return the main hand
     */
    ItemStack getMainHand();

    /**
     * Sets main hand.
     *
     * @param itemStack the item stack
     */
    void setMainHand(ItemStack itemStack);

    /**
     * Gets off hand.
     *
     * @return the off hand
     */
    ItemStack getOffHand();

    /**
     * Sets off hand.
     *
     * @param itemStack the item stack
     */
    void setOffHand(ItemStack itemStack);

    /**
     * Respawn.
     */
    void respawn();

    /**
     * Show player.
     *
     * @param player the player
     */
    void showPlayer(CPlayer player);

    /**
     * Hide player.
     *
     * @param player the player
     */
    void hidePlayer(CPlayer player);

    /**
     * Can see boolean.
     *
     * @param player the player
     * @return if player can be seen
     */
    boolean canSee(CPlayer player);

    /**
     * Can see boolean.
     *
     * @param player the player
     * @return if player can be seen
     */
    boolean canSee(Player player);

    /**
     * Gets action bar.
     *
     * @return the action bar
     */
    CPlayerActionBarManager getActionBar();

    /**
     * Gets boss bar.
     *
     * @return the boss bar
     */
    CPlayerBossBarManager getBossBar();

    /**
     * Gets header footer.
     *
     * @return the header footer
     */
    CPlayerHeaderFooterManager getHeaderFooter();

    /**
     * Gets particles.
     *
     * @return the particles
     */
    CPlayerParticlesManager getParticles();

    /**
     * Gets resource pack.
     *
     * @return the resource pack
     */
    CPlayerResourcePackManager getResourcePack();

    /**
     * Gets scoreboard.
     *
     * @return the scoreboard
     */
    CPlayerScoreboardManager getScoreboard();

    /**
     * Gets title.
     *
     * @return the title
     */
    CPlayerTitleManager getTitle();

    /**
     * Send packet.
     *
     * @param packet the packet
     */
    void sendPacket(AbstractPacket packet);

    /**
     * Send to server.
     *
     * @param server the server
     */
    void sendToServer(String server);

    /**
     * Sets locale.
     *
     * @param locale the locale
     */
    void setLocale(String locale);

    /**
     * Gets locale.
     *
     * @return the locale
     */
    String getLocale();

    /**
     * Gets bukkit player.
     *
     * @return the bukkit player
     */
    Player getBukkitPlayer();

    /**
     * Gets unique id.
     *
     * @return the unique id
     */
    UUID getUniqueId();

    /**
     * Gets uuid.
     *
     * @return the uuid
     */
    UUID getUuid();

    /**
     * Sets status.
     *
     * @param status the status
     */
    void setStatus(PlayerStatus status);

    /**
     * Gets status.
     *
     * @return the status
     */
    PlayerStatus getStatus();

    /**
     * Gets rank.
     *
     * @return the rank
     */
    Rank getRank();

    /**
     * Sets rank.
     *
     * @param rank the rank
     */
    void setRank(Rank rank);

    /**
     * Sets texture hash.
     *
     * @param textureHash the texture hash
     */
    void setTextureHash(String textureHash);

    /**
     * Gets texture hash.
     *
     * @return the texture hash
     */
    String getTextureHash();

    /**
     * Sets pack.
     *
     * @param pack the pack
     */
    void setPack(String pack);

    /**
     * Gets pack.
     *
     * @return the pack
     */
    String getPack();

    /**
     * Check if player has an achievement
     *
     * @param i achievement ID
     * @return if player has achievement
     */
    boolean hasAchievement(int i);

    /**
     * Give player an achievement
     *
     * @param i ID of achievement
     */
    void giveAchievement(int i);

    /**
     * Get the selected block within a range
     *
     * @param range
     * @return the selected block
     */
    Block getTargetBlock(int range);

    /**
     * Get the player's ping to the server
     *
     * @return player's ping
     */
    int getPing();

    /**
     * Get whether a player can fly
     *
     * @return flight value
     */
    boolean getAllowFlight();

    /**
     * Set whether a player can fly
     *
     * @param fly flight value
     */
    void setAllowFlight(boolean fly);

    /**
     * Get whether a player is flying
     *
     * @return flying value
     */
    boolean isFlying();

    /**
     * Set whether a player is flying
     *
     * @param fly flying value
     */
    void setFlying(boolean fly);
}

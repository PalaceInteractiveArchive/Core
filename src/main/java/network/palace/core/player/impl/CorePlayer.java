package network.palace.core.player.impl;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import network.palace.core.Core;
import network.palace.core.config.LanguageFormatter;
import network.palace.core.packets.AbstractPacket;
import network.palace.core.player.*;
import network.palace.core.plugin.Plugin;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.UUID;
import java.util.logging.Level;

public class CorePlayer implements CPlayer {

    @Getter private final UUID uuid;
    private final String name;
    @Getter @Setter private Rank rank = Rank.SETTLER;
    @Getter @Setter private String locale = "en_US";
    @Getter @Setter private PlayerStatus status = PlayerStatus.LOGIN;
    @Getter private CPlayerActionBarManager actionBar = new CorePlayerActionBarManager(this);
    @Getter private CPlayerBossBarManager bossBar = new CorePlayerBossBarManager(this);
    @Getter private CPlayerHeaderFooterManager headerFooter = new CorePlayerHeaderFooterManager(this);
    @Getter private CPlayerScoreboardManager scoreboard = new CorePlayerScoreboardManager(this);
    @Getter private CPlayerTitleManager title = new CorePlayerTitleManager(this);
    @Getter private CPlayerParticlesManager particles = new CorePlayerParticlesManager(this);
    @Getter private CPlayerResourcePackManager resourcePack = new CorePlayerResourcePackManager(this);
    @Getter @Setter String textureHash = "";

    public CorePlayer(UUID uuid, String name, Rank rank) {
        this.uuid = uuid;
        this.name = name;
        this.rank = rank;
    }

    @Override
    public String getName() {
        if (getStatus() != PlayerStatus.JOINED) return name;
        if (getBukkitPlayer() == null) return name;
        return getBukkitPlayer().getName();
    }

    @Override
    public boolean isOnline() {
        return getStatus() == PlayerStatus.JOINED && getBukkitPlayer() != null && getBukkitPlayer().isOnline();
    }

    @Override
    @SuppressWarnings("deprecation")
    public void setMaxHealth(double health) {
        if (getStatus() != PlayerStatus.JOINED) return;
        if (getBukkitPlayer() == null) return;
        getBukkitPlayer().setMaxHealth(health);
    }

    @Override
    public void setHealth(double health) {
        if (getStatus() != PlayerStatus.JOINED) return;
        if (getBukkitPlayer() == null) return;
        getBukkitPlayer().setHealth(health);
    }

    @Override
    public GameMode getGamemode() {
        if (getStatus() != PlayerStatus.JOINED) return GameMode.SURVIVAL;
        if (getBukkitPlayer() == null) return GameMode.SURVIVAL;
        return getBukkitPlayer().getGameMode();
    }

    @Override
    public void setGamemode(GameMode gamemode) {
        if (getStatus() != PlayerStatus.JOINED) return;
        if (getBukkitPlayer() == null) return;
        getBukkitPlayer().setGameMode(gamemode);
    }

    @Override
    public Location getLocation() {
        if (getStatus() != PlayerStatus.JOINED) return new Location(Core.getDefaultWorld(), 0, 64, 0);
        if (getBukkitPlayer() == null) return new Location(Core.getDefaultWorld(), 0, 64, 0);
        return getBukkitPlayer().getLocation();
    }

    @Override
    public void teleport(Location location) {
        if (getStatus() != PlayerStatus.JOINED) return;
        if (getBukkitPlayer() == null) return;
        getBukkitPlayer().teleport(location);
    }

    @Override
    public void sendMessage(String message) {
        if (getStatus() != PlayerStatus.JOINED) return;
        if (getBukkitPlayer() == null) return;
        getBukkitPlayer().sendMessage(message);
    }

    @Override
    public void sendFormatMessage(JavaPlugin plugin, String key) {
        if (getStatus() != PlayerStatus.JOINED) return;
        if (getBukkitPlayer() == null) return;
        LanguageFormatter languageFormatter = null;
        if (plugin instanceof Core) {
            languageFormatter = Core.getLanguageFormatter();
        } else if (plugin instanceof Plugin) {
            languageFormatter = ((Plugin) plugin).getLanguageFormatter();
        }
        if (languageFormatter == null) {
            plugin.getLogger().log(Level.SEVERE, "PROBLEM GETTING LANGUAGE FORMATTER for key: " + key);
            return;
        }
        String message = languageFormatter.getFormat(getLocale(), key);
        if (message.equals("")) {
            plugin.getLogger().log(Level.SEVERE, "MESSAGE NULL for key: " + key);
            return;
        }
        getBukkitPlayer().sendMessage(message);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void resetPlayer() {
        if (getStatus() != PlayerStatus.JOINED) return;
        if (getBukkitPlayer() == null) return;
        Player player = getBukkitPlayer();
        player.setItemOnCursor(null);
        player.getInventory().clear();
        player.getInventory().setArmorContents(new ItemStack[4]);
        player.getInventory().setHeldItemSlot(0);
        player.getInventory().setItemInMainHand(null);
        player.getInventory().setItemInOffHand(null);
        player.setFoodLevel(20);
        player.setExhaustion(0);
        player.setSaturation(20);
        player.setHealth(20);
        player.setFallDistance(0);
        player.setFireTicks(0);
        player.resetMaxHealth();
        player.setExp(0);
        player.setTotalExperience(0);
        player.setLevel(0);
        player.setRemainingAir(20);
        player.setAllowFlight(false);
        player.setFlying(false);
        player.setSneaking(false);
        player.setSprinting(false);
        player.setVelocity(new Vector());
        player.setFallDistance(0f);
        player.resetPlayerTime();
        player.resetPlayerWeather();
        player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
        player.updateInventory();
    }

    @Override
    public void resetManagers() {
        if (getStatus() != PlayerStatus.JOINED) return;
        getBossBar().remove();
        getHeaderFooter().hide();
        getTitle().hide();
    }

    @Override
    public void setDisplayName(String name) {
        if (getStatus() != PlayerStatus.JOINED) return;
        if (getBukkitPlayer() == null) return;
        getBukkitPlayer().setDisplayName(name);
    }

    @Override
    public void setInventorySlot(int slot, ItemStack stack) {
        if (getInventory() == null) return;
        getInventory().setItem(slot, stack);
    }

    @Override
    public void addToInventory(ItemStack... itemStacks) {
        if (getInventory() == null) return;
        getInventory().addItem(itemStacks);
    }

    @Override
    public boolean doesInventoryContain(Material material) {
        return getInventory() != null && getInventory().contains(material);
    }

    @Override
    public void removeFromInventory(Material material) {
        if (getInventory() == null) return;
        getInventory().remove(material);
    }

    @Override
    public ItemStack getHelmet() {
        if (getInventory() == null) return new ItemStack(Material.AIR);
        return getInventory().getHelmet();
    }

    @Override
    public void setHelmet(ItemStack itemStack) {
        if (getInventory() == null) return;
        getInventory().setHelmet(itemStack);
    }

    @Override
    public ItemStack getChestplate() {
        if (getInventory() == null) return new ItemStack(Material.AIR);
        return getInventory().getChestplate();
    }

    @Override
    public void setChestplate(ItemStack itemStack) {
        if (getInventory() == null) return;
        getInventory().setChestplate(itemStack);
    }

    @Override
    public ItemStack getLeggings() {
        if (getInventory() == null) return new ItemStack(Material.AIR);
        return getInventory().getLeggings();
    }

    @Override
    public void setLeggings(ItemStack itemStack) {
        if (getInventory() == null) return;
        getInventory().setLeggings(itemStack);
    }

    @Override
    public ItemStack getBoots() {
        if (getInventory() == null) return new ItemStack(Material.AIR);
        return getInventory().getBoots();
    }

    @Override
    public void setBoots(ItemStack itemStack) {
        if (getInventory() == null) return;
        getInventory().setBoots(itemStack);
    }

    @Override
    public ItemStack getMainHand() {
        if (getInventory() == null) return new ItemStack(Material.AIR);
        return getInventory().getItemInMainHand();
    }

    @Override
    public void setMainHand(ItemStack itemStack) {
        if (getInventory() == null) return;
        getInventory().setItemInMainHand(itemStack);
    }

    @Override
    public ItemStack getOffHand() {
        if (getInventory() == null) return new ItemStack(Material.AIR);
        return getInventory().getItemInOffHand();
    }

    @Override
    public void setOffHand(ItemStack itemStack) {
        if (getInventory() == null) return;
        getInventory().setItemInOffHand(itemStack);
    }

    @Override
    public PlayerInventory getInventory() {
        if (getStatus() != PlayerStatus.JOINED) return null;
        if (getBukkitPlayer() == null) return null;
        return getBukkitPlayer().getInventory();
    }

    @Override
    public void openInventory(Inventory inventory) {
        if (getStatus() != PlayerStatus.JOINED) return;
        if (getBukkitPlayer() == null) return;
        getBukkitPlayer().openInventory(inventory);
    }

    @Override
    public void closeInventory() {
        if (getStatus() != PlayerStatus.JOINED) return;
        if (getBukkitPlayer() == null) return;
        getBukkitPlayer().closeInventory();
    }

    @Override
    public void respawn() {
        if (getStatus() != PlayerStatus.JOINED) return;
        if (getBukkitPlayer() == null) return;
        getBukkitPlayer().spigot().respawn();
    }

    @Override
    public void showPlayer(CPlayer player) {
        if (getStatus() != PlayerStatus.JOINED) return;
        if (player == null) return;
        if (player.getStatus() != PlayerStatus.JOINED) return;
        if (getBukkitPlayer() == null) return;
        getBukkitPlayer().showPlayer(player.getBukkitPlayer());
    }

    @Override
    public void hidePlayer(CPlayer player) {
        if (getStatus() != PlayerStatus.JOINED) return;
        if (player == null) return;
        if (player.getStatus() != PlayerStatus.JOINED) return;
        if (getBukkitPlayer() == null) return;
        getBukkitPlayer().hidePlayer(player.getBukkitPlayer());
    }

    @Override
    public void sendPacket(AbstractPacket packet) {
        if (getStatus() != PlayerStatus.JOINED) return;
        if (getBukkitPlayer() == null) return;
        packet.sendPacket(getBukkitPlayer());
    }

    @Override
    public void sendToServer(String server) {
        if (getStatus() != PlayerStatus.JOINED) return;
        if (getBukkitPlayer() == null) return;
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(server);
        getBukkitPlayer().sendPluginMessage(Core.getInstance(), "BungeeCord", out.toByteArray());
    }

    @Override
    public Player getBukkitPlayer() {
        if (getStatus() != PlayerStatus.JOINED) return null;
        return Bukkit.getPlayer(getUuid());
    }
}
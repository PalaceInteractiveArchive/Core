package com.thepalace.core.player.impl;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.thepalace.core.Core;
import com.thepalace.core.config.LanguageFormatter;
import com.thepalace.core.packets.AbstractPacket;
import com.thepalace.core.plugin.Plugin;
import com.thepalace.core.player.*;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.UUID;
import java.util.logging.Level;

public class CorePlayer implements CPlayer {

    @Getter private UUID uuid;
    @Getter @Setter private String locale = "en_US";
    @Getter private CPlayerActionBarManager actionBar = new CorePlayerActionBarManager(this);
    @Getter private CPlayerBossBarManager bossBar = new CorePlayerBossBarManager(this);
    @Getter private CPlayerHeaderFooterManager headerFooter = new CorePlayerHeaderFooterManager(this);
    @Getter private CPlayerScoreboardManager scoreboard = new CorePlayerScoreboardManager(this);
    @Getter private CPlayerTitleManager title = new CorePlayerTitleManager(this);
    @Getter private CPlayerParticlesManager particles = new CorePlayerParticlesManager(this);
    @Getter private CPlayerResourcePackManager resourcePack = new CorePlayerResourcePackManager(this);

    public CorePlayer(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public String getName() {
        return getBukkitPlayer().getName();
    }

    @Override
    public boolean isOnline() {
        Player bukkitPlayer = getBukkitPlayer();
        return bukkitPlayer == null || bukkitPlayer.isOnline();
    }

    @Override
    public void setMaxHealth(double health) {
        getBukkitPlayer().setMaxHealth(health);
    }

    @Override
    public void setHealth(double health) {
        getBukkitPlayer().setHealth(health);
    }

    @Override
    public GameMode getGamemode() {
        return getBukkitPlayer().getGameMode();
    }

    @Override
    public void setGamemode(GameMode gamemode) {
        getBukkitPlayer().setGameMode(gamemode);
    }

    @Override
    public Location getLocation() {
        return getBukkitPlayer().getLocation();
    }

    @Override
    public void setLocation(Location location) {
        getBukkitPlayer().teleport(location);
    }

    @Override
    public void teleport(Location location) {
        getBukkitPlayer().teleport(location);
    }

    @Override
    public void sendMessage(String message) {
        getBukkitPlayer().sendMessage(message);
    }

    @Override
    public void sendFormatMessage(JavaPlugin plugin, String key) {
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
        if (message == null) {
            plugin.getLogger().log(Level.SEVERE, "MESSAGE NULL for key: " + key);
            return;
        }
        getBukkitPlayer().sendMessage(message);
    }

    @Override
    public void resetPlayer() {
        Player bukkitPlayer = getBukkitPlayer();
        bukkitPlayer.setItemOnCursor(null);
        bukkitPlayer.getInventory().clear();
        bukkitPlayer.getInventory().setArmorContents(new ItemStack[4]);
        bukkitPlayer.getInventory().setHeldItemSlot(0);
        bukkitPlayer.getInventory().setItemInMainHand(null);
        bukkitPlayer.getInventory().setItemInOffHand(null);
        bukkitPlayer.setFoodLevel(20);
        bukkitPlayer.setExhaustion(0);
        bukkitPlayer.setSaturation(20);
        bukkitPlayer.setHealth(20);
        bukkitPlayer.setFallDistance(0);
        bukkitPlayer.setFireTicks(0);
        bukkitPlayer.resetMaxHealth();
        bukkitPlayer.setExp(0);
        bukkitPlayer.setTotalExperience(0);
        bukkitPlayer.setLevel(0);
        bukkitPlayer.setRemainingAir(20);
        bukkitPlayer.setAllowFlight(false);
        bukkitPlayer.setFlying(false);
        bukkitPlayer.setSneaking(false);
        bukkitPlayer.setSprinting(false);
        bukkitPlayer.setVelocity(new Vector());
        bukkitPlayer.setFallDistance(0f);
        bukkitPlayer.resetPlayerTime();
        bukkitPlayer.resetPlayerWeather();
        bukkitPlayer.getActivePotionEffects().forEach(potionEffect -> bukkitPlayer.removePotionEffect(potionEffect.getType()));
        bukkitPlayer.updateInventory();
    }

    @Override
    public void resetManagers() {
        getBossBar().remove();
        getHeaderFooter().hide();
        getTitle().hide();
    }

    @Override
    public PlayerInventory getInventory() {
        return getBukkitPlayer().getInventory();
    }

    @Override
    public void openInventory(Inventory inventory) {
        getBukkitPlayer().openInventory(inventory);
    }

    @Override
    public void closeInventory() {
        getBukkitPlayer().closeInventory();
    }

    @Override
    public void respawn() {
        getBukkitPlayer().spigot().respawn();
    }

    @Override
    public void sendPacket(AbstractPacket packet) {
        packet.sendPacket(getBukkitPlayer());
    }

    @Override
    public void sendToServer(String server) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(server);
        getBukkitPlayer().sendPluginMessage(Core.getInstance(), "BungeeCord", out.toByteArray());
    }

    @Override
    public Player getBukkitPlayer() {
        return Bukkit.getPlayer(getUuid());
    }
}

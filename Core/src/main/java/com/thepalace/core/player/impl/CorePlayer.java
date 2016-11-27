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

    @Getter private final UUID uuid;
    @Getter @Setter private String locale = "en_US";
    @Getter @Setter private PlayerStatus status = PlayerStatus.LOGIN;
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
        if (status != PlayerStatus.JOINED) return "";
        return getBukkitPlayer().getName();
    }

    @Override
    public boolean isOnline() {
        return status == PlayerStatus.JOINED && (getBukkitPlayer() == null || getBukkitPlayer().isOnline());
    }

    @Override
    public void setMaxHealth(double health) {
        if (status != PlayerStatus.JOINED) return;
        getBukkitPlayer().setMaxHealth(health);
    }

    @Override
    public void setHealth(double health) {
        if (status != PlayerStatus.JOINED) return;
        getBukkitPlayer().setHealth(health);
    }

    @Override
    public GameMode getGamemode() {
        if (status != PlayerStatus.JOINED) return GameMode.SURVIVAL;
        return getBukkitPlayer().getGameMode();
    }

    @Override
    public void setGamemode(GameMode gamemode) {
        if (status != PlayerStatus.JOINED) return;
        getBukkitPlayer().setGameMode(gamemode);
    }

    @Override
    public Location getLocation() {
        if (status != PlayerStatus.JOINED) return new Location(Core.getWorld("world"), 0, 64, 0);
        return getBukkitPlayer().getLocation();
    }

    @Override
    public void teleport(Location location) {
        if (status != PlayerStatus.JOINED) return;
        getBukkitPlayer().teleport(location);
    }

    @Override
    public void sendMessage(String message) {
        if (status != PlayerStatus.JOINED) return;
        getBukkitPlayer().sendMessage(message);
    }

    @Override
    public void sendFormatMessage(JavaPlugin plugin, String key) {
        if (status != PlayerStatus.JOINED) return;
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
        if (status != PlayerStatus.JOINED) return;
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
        if (status != PlayerStatus.JOINED) return;
        getBossBar().remove();
        getHeaderFooter().hide();
        getTitle().hide();
    }

    @Override
    public PlayerInventory getInventory() {
        if (status != PlayerStatus.JOINED) return null;
        return getBukkitPlayer().getInventory();
    }

    @Override
    public void openInventory(Inventory inventory) {
        if (status != PlayerStatus.JOINED) return;
        getBukkitPlayer().openInventory(inventory);
    }

    @Override
    public void closeInventory() {
        if (status != PlayerStatus.JOINED) return;
        getBukkitPlayer().closeInventory();
    }

    @Override
    public void respawn() {
        if (status != PlayerStatus.JOINED) return;
        getBukkitPlayer().spigot().respawn();
    }

    @Override
    public void sendPacket(AbstractPacket packet) {
        if (status != PlayerStatus.JOINED) return;
        packet.sendPacket(getBukkitPlayer());
    }

    @Override
    public void sendToServer(String server) {
        if (status != PlayerStatus.JOINED) return;
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(server);
        getBukkitPlayer().sendPluginMessage(Core.getInstance(), "BungeeCord", out.toByteArray());
    }

    @Override
    public Player getBukkitPlayer() {
        if (status != PlayerStatus.JOINED) return null;
        return Bukkit.getPlayer(getUuid());
    }
}

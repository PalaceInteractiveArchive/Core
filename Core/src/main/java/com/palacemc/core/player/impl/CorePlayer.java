package com.palacemc.core.player.impl;

import com.palacemc.core.packets.AbstractPacket;
import com.palacemc.core.player.*;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.UUID;

public class CorePlayer implements CPlayer {

    @Getter private UUID uuid;
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
    public void sendMessage(String... messages) {
        Arrays.stream(messages).forEach(message -> getBukkitPlayer().sendMessage(message));
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
        getScoreboard().reset();
        getTitle().hide();
    }

    @Override
    public void sendPacket(AbstractPacket packet) {
        packet.sendPacket(getBukkitPlayer());
    }

    @Override
    public Player getBukkitPlayer() {
        return Bukkit.getPlayer(getUuid());
    }
}

package network.palace.core.player.impl;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import lombok.Getter;
import lombok.Setter;
import network.palace.core.Core;
import network.palace.core.config.LanguageManager;
import network.palace.core.economy.CurrencyType;
import network.palace.core.events.GameStatisticChangeEvent;
import network.palace.core.packets.AbstractPacket;
import network.palace.core.player.*;
import network.palace.core.player.impl.managers.*;
import network.palace.core.plugin.Plugin;
import network.palace.core.tracking.GameType;
import network.palace.core.tracking.StatisticType;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.map.MapView;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Implementation of CPlayer
 *
 * @see network.palace.core.player.CPlayer
 */
public class CorePlayer implements CPlayer {

    @Getter private final UUID uuid;
    private final String name;
    @Getter @Setter private int protocolId = -1;
    @Getter @Setter private Rank rank;
    @Getter @Setter private SponsorTier sponsorTier;
    @Getter @Setter private String locale;
    @Getter @Setter private PlayerStatus status = PlayerStatus.LOGIN;
    @Getter private CPlayerAchievementManager achievementManager;
    @Getter @Setter private CPlayerScoreboardManager scoreboard = new CorePlayerScoreboardManager(this);
    @Getter private CPlayerActionBarManager actionBar = new CorePlayerActionBarManager(this);
    @Getter private CPlayerBossBarManager bossBar = new CorePlayerBossBarManager(this);
    @Getter private CPlayerHeaderFooterManager headerFooter = new CorePlayerHeaderFooterManager(this);
    @Getter private CPlayerTitleManager title = new CorePlayerTitleManager(this);
    @Getter private CPlayerParticlesManager particles = new CorePlayerParticlesManager(this);
    @Getter private CPlayerResourcePackManager resourcePack = new CorePlayerResourcePackManager(this);
    @Getter private CPlayerRegistry registry = new CorePlayerRegistry(this);
    @Getter @Setter private String textureValue = "";
    @Getter @Setter private String textureSignature = "";
    @Getter @Setter private String pack = "none";
    @Getter private final long joinTime = System.currentTimeMillis();
    private final List<Integer> queuedAchievements = new ArrayList<>();

    @Getter @Setter private int honor;
    @Getter @Setter private int previousHonorLevel;

    /**
     * Instantiates a new Core player.
     *
     * @param uuid the uuid
     * @param name the name
     * @param rank the rank
     */
    public CorePlayer(UUID uuid, String name, Rank rank, SponsorTier sponsorTier, String locale) {
        this.uuid = uuid;
        this.name = name;
        this.rank = rank;
        this.sponsorTier = sponsorTier;
        this.locale = locale;
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
    public void setOp(boolean isOp) {
        if (getStatus() != PlayerStatus.JOINED) return;
        if (getBukkitPlayer() == null) return;
        getBukkitPlayer().setOp(isOp);
    }

    @Override
    public boolean isOp() {
        return getStatus() == PlayerStatus.JOINED && getBukkitPlayer() != null && getBukkitPlayer().isOp();
    }

    @Override
    public int getEntityId() {
        if (getStatus() != PlayerStatus.JOINED) return 0;
        if (getBukkitPlayer() == null) return 0;
        return getBukkitPlayer().getEntityId();
    }

    @Override
    public void playSound(Location location, Sound sound, float volume, float pitch) {
        if (getStatus() != PlayerStatus.JOINED) return;
        if (getBukkitPlayer() == null) return;
        if (location == null) return;
        if (sound == null) return;
        getBukkitPlayer().playSound(location, sound, volume, pitch);
    }

    @SuppressWarnings("deprecation")
    @Override
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
    public double getHealth() {
        if (getStatus() != PlayerStatus.JOINED) return 20;
        if (getBukkitPlayer() == null) return 20;

        return getBukkitPlayer().getHealth();
    }

    @SuppressWarnings("deprecation")
    @Override
    public double getMaxHealth() {
        if (getStatus() != PlayerStatus.JOINED) return 20;
        if (getBukkitPlayer() == null) return 20;

        return getBukkitPlayer().getMaxHealth();
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
        if (gamemode == null) gamemode = GameMode.ADVENTURE;
        getBukkitPlayer().setGameMode(gamemode);
    }

    @Override
    public Location getLocation() {
        if (getStatus() != PlayerStatus.JOINED) return new Location(Core.getDefaultWorld(), 0, 64, 0);
        if (getBukkitPlayer() == null) return new Location(Core.getDefaultWorld(), 0, 64, 0);
        return getBukkitPlayer().getLocation();
    }

    @Override
    public World getWorld() {
        if (getStatus() != PlayerStatus.JOINED) return Bukkit.getWorlds().get(0);
        if (getBukkitPlayer() == null) return Bukkit.getWorlds().get(0);
        return getBukkitPlayer().getWorld();
    }

    @Override
    public void teleport(Location location) {
        if (getStatus() != PlayerStatus.JOINED) return;
        if (getBukkitPlayer() == null) return;
        if (location == null) return;
        if (location.getWorld() == null) return;
        getBukkitPlayer().teleport(location);
    }

    @Override
    public void teleport(Location location, PlayerTeleportEvent.TeleportCause cause) {
        if (getStatus() != PlayerStatus.JOINED) return;
        if (getBukkitPlayer() == null) return;
        if (location == null || cause == null) return;
        if (location.getWorld() == null) return;
        getBukkitPlayer().teleport(location, cause);
    }

    @Override
    public void teleport(CPlayer tp) {
        if (getStatus() != PlayerStatus.JOINED) return;
        if (getBukkitPlayer() == null) return;
        if (tp == null) return;
        Location location = tp.getLocation();
        teleport(location);
    }

    @Override
    public void sendMessage(String message) {
        if (getStatus() != PlayerStatus.JOINED) return;
        if (getBukkitPlayer() == null) return;
        if (message == null) return;
        getBukkitPlayer().sendMessage(message);
    }

    @Override
    public String getFormattedMessage(String key) {
        if (getStatus() != PlayerStatus.JOINED) return "";
        LanguageManager languageManager = Core.getLanguageFormatter();
        if (languageManager == null) {
            Core.logMessage("Language Formatter", "PROBLEM GETTING LANGUAGE FORMATTER for key: " + key);
            return "";
        }
        String message = languageManager.getFormat(getLocale(), key);
        if (message.isEmpty()) {
            Core.logMessage("Language Formatter", "MESSAGE NULL for key: " + key);
            return "";
        }
        return message;
    }

    @Override
    public void sendFormatMessage(String key) {
        if (getStatus() != PlayerStatus.JOINED) return;
        if (getBukkitPlayer() == null) return;
        String message = getFormattedMessage(key);
        if (message.isEmpty()) {
            Core.logMessage("Language Formatter", "MESSAGE NULL for key: " + key);
            return;
        }
        getBukkitPlayer().sendMessage(message);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void resetPlayer() {
        if (getStatus() != PlayerStatus.JOINED) return;
        if (getBukkitPlayer() == null) return;
        Player player = getBukkitPlayer();
        player.setItemOnCursor(null);
        player.getInventory().clear();
        player.getInventory().setArmorContents(new ItemStack[4]);
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
        player.setRemainingAir(300);
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
        getScoreboard().clear();
    }

    @Override
    public void setDisplayName(String name) {
        if (getStatus() != PlayerStatus.JOINED) return;
        if (getBukkitPlayer() == null) return;
        if (name == null) name = "";
        getBukkitPlayer().setDisplayName(name);
    }

    @Override
    public void setInventorySlot(int slot, ItemStack stack) {
        if (getInventory() == null) return;
        if (stack == null) stack = new ItemStack(Material.AIR);
        getInventory().setItem(slot, stack);
    }

    @Override
    public void addToInventory(ItemStack... stacks) {
        if (getInventory() == null) return;
        if (stacks == null) return;
        getInventory().addItem(stacks);
    }

    @Override
    public boolean doesInventoryContain(Material material) {
        return material != null && getInventory() != null && getInventory().contains(material);
    }

    @Override
    public void removeFromInventory(Material material) {
        if (getInventory() == null) return;
        if (material == null) return;
        getInventory().remove(material);
    }

    @Override
    public ItemStack getHelmet() {
        if (getInventory() == null) return new ItemStack(Material.AIR);
        return getInventory().getHelmet();
    }

    @Override
    public void setHelmet(ItemStack itemStack) {
        if (getInventory() == null) itemStack = new ItemStack(Material.AIR);
        getInventory().setHelmet(itemStack);
    }

    @Override
    public ItemStack getChestplate() {
        if (getInventory() == null) return new ItemStack(Material.AIR);
        return getInventory().getChestplate();
    }

    @Override
    public void setChestplate(ItemStack itemStack) {
        if (getInventory() == null) itemStack = new ItemStack(Material.AIR);
        getInventory().setChestplate(itemStack);
    }

    @Override
    public ItemStack getLeggings() {
        if (getInventory() == null) return new ItemStack(Material.AIR);
        return getInventory().getLeggings();
    }

    @Override
    public void setLeggings(ItemStack itemStack) {
        if (getInventory() == null) itemStack = new ItemStack(Material.AIR);
        getInventory().setLeggings(itemStack);
    }

    @Override
    public ItemStack getBoots() {
        if (getInventory() == null) return new ItemStack(Material.AIR);
        return getInventory().getBoots();
    }

    @Override
    public void setBoots(ItemStack itemStack) {
        if (getInventory() == null) itemStack = new ItemStack(Material.AIR);
        getInventory().setBoots(itemStack);
    }

    @Override
    public ItemStack getMainHand() {
        if (getInventory() == null) return new ItemStack(Material.AIR);
        return getInventory().getItemInMainHand();
    }

    @Override
    public void setMainHand(ItemStack itemStack) {
        if (getInventory() == null) itemStack = new ItemStack(Material.AIR);
        getInventory().setItemInMainHand(itemStack);
    }

    @Override
    public ItemStack getOffHand() {
        if (getInventory() == null) return new ItemStack(Material.AIR);
        return getInventory().getItemInOffHand();
    }

    @Override
    public void setOffHand(ItemStack itemStack) {
        if (getInventory() == null) itemStack = new ItemStack(Material.AIR);
        getInventory().setItemInOffHand(itemStack);
    }

    @Override
    public PlayerInventory getInventory() {
        if (getStatus() != PlayerStatus.JOINED) return null;
        if (getBukkitPlayer() == null) return null;
        return getBukkitPlayer().getInventory();
    }

    @Override
    public void updateInventory() {
        if (getStatus() != PlayerStatus.JOINED) return;
        if (getBukkitPlayer() == null) return;
        getBukkitPlayer().updateInventory();
    }

    @Override
    public ItemStack getItem(int slot) {
        if (getStatus() != PlayerStatus.JOINED) return null;
        if (getBukkitPlayer() == null) return null;
        return getBukkitPlayer().getInventory().getItem(slot);
    }

    @Override
    public ItemStack getItemInMainHand() {
        if (getStatus() != PlayerStatus.JOINED) return null;
        if (getBukkitPlayer() == null) return null;
        return getBukkitPlayer().getInventory().getItemInMainHand();
    }

    @Override
    public ItemStack getItemInOffHand() {
        if (getStatus() != PlayerStatus.JOINED) return null;
        if (getBukkitPlayer() == null) return null;
        return getBukkitPlayer().getInventory().getItemInOffHand();
    }

    @Override
    public int getHeldItemSlot() {
        if (getStatus() != PlayerStatus.JOINED) return 0;
        if (getBukkitPlayer() == null) return 0;
        return getBukkitPlayer().getInventory().getHeldItemSlot();
    }

    @Override
    public void setHeldItemSlot(int slot) {
        if (getStatus() != PlayerStatus.JOINED) return;
        if (getBukkitPlayer() == null) return;
        getBukkitPlayer().getInventory().setHeldItemSlot(slot);
    }

    @Override
    public void openInventory(Inventory inventory) {
        if (getStatus() != PlayerStatus.JOINED) return;
        if (getBukkitPlayer() == null) return;
        if (inventory == null) return;
        getBukkitPlayer().openInventory(inventory);
    }

    @Override
    public void closeInventory() {
        if (getStatus() != PlayerStatus.JOINED) return;
        if (getBukkitPlayer() == null) return;
        getBukkitPlayer().closeInventory();
    }

    @Override
    public boolean hasPermission(String node) {
        return getStatus() == PlayerStatus.JOINED && getBukkitPlayer() != null && getBukkitPlayer().hasPermission(node);
    }

    @Override
    public void respawn() {
        if (getStatus() != PlayerStatus.JOINED) return;
        if (getBukkitPlayer() == null) return;
        if (getBukkitPlayer().spigot() == null) return;
        getBukkitPlayer().spigot().respawn();
    }

    @Override
    @Deprecated
    public void showPlayer(CPlayer player) {
        showPlayer(null, player);
    }

    @Override
    public void showPlayer(org.bukkit.plugin.Plugin plugin, CPlayer player) {
        if (getStatus() != PlayerStatus.JOINED) return;
        if (player == null) return;
        if (player.getStatus() != PlayerStatus.JOINED) return;
        if (getBukkitPlayer() == null) return;
        if (plugin == null) plugin = Core.getInstance();
        getBukkitPlayer().showPlayer(plugin, player.getBukkitPlayer());
//        getBukkitPlayer().showPlayer(player.getBukkitPlayer());
    }

    @Override
    @Deprecated
    public void hidePlayer(CPlayer player) {
        hidePlayer(null, player);
    }

    @Override
    public void hidePlayer(org.bukkit.plugin.Plugin plugin, CPlayer player) {
        if (getStatus() != PlayerStatus.JOINED) return;
        if (player == null) return;
        if (player.getStatus() != PlayerStatus.JOINED) return;
        if (getBukkitPlayer() == null) return;
        if (plugin == null) plugin = Core.getInstance();
        getBukkitPlayer().hidePlayer(plugin, player.getBukkitPlayer());
//        getBukkitPlayer().hidePlayer(player.getBukkitPlayer());
    }

    @Override
    public boolean canSee(CPlayer player) {
        return canSee(player.getBukkitPlayer());
    }

    @Override
    public boolean canSee(Player player) {
        return getStatus() == PlayerStatus.JOINED && getBukkitPlayer() != null && player != null && getBukkitPlayer().canSee(player);
    }

    @Override
    public void sendPacket(AbstractPacket packet) {
        if (getStatus() != PlayerStatus.JOINED) return;
        if (getBukkitPlayer() == null) return;
        if (packet == null) return;
        packet.sendPacket(Core.getPlayerManager().getPlayer(getBukkitPlayer()));
    }

    @Override
    public void sendToServer(String server) {
        if (getStatus() != PlayerStatus.JOINED) return;
        if (getBukkitPlayer() == null) return;
        if (server == null) return;
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(server);
        getBukkitPlayer().sendPluginMessage(Core.getInstance(), "BungeeCord", out.toByteArray());
    }

    @Override
    public Player getBukkitPlayer() {
        if (getStatus() != PlayerStatus.JOINED) return null;
        return Bukkit.getPlayer(getUniqueId());
    }

    @Override
    public UUID getUniqueId() {
        return getUuid();
    }

    @Override
    public boolean hasAchievement(int i) {
        return getStatus() == PlayerStatus.JOINED && getBukkitPlayer() != null && achievementManager != null && achievementManager.hasAchievement(i);
    }

    @Override
    public void giveAchievement(int i) {
        if (getStatus() != PlayerStatus.JOINED) return;
        if (getBukkitPlayer() == null) return;
        if (achievementManager == null) {
            queuedAchievements.add(i);
            return;
        }
        achievementManager.giveAchievement(i);
    }

    @Override
    public Block getTargetBlock(int range) {
        if (getBukkitPlayer() == null) return null;
        return getBukkitPlayer().getTargetBlock(null, range);
    }

    @Override
    public int getPing() {
        if (getStatus() != PlayerStatus.JOINED) return 0;
        if (getBukkitPlayer() == null) return 0;
        try {
            Object craftPlayer = Class.forName("org.bukkit.craftbukkit." + Core.getMinecraftVersion() +
                    ".entity.CraftPlayer").cast(getBukkitPlayer());
            Method m = craftPlayer.getClass().getDeclaredMethod("getHandle");
            Object entityPlayer = m.invoke(craftPlayer);
            Field ping = entityPlayer.getClass().getDeclaredField("ping");
            ping.setAccessible(true);
            return (int) ping.get(entityPlayer);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException |
                NoSuchFieldException e) {
            return 0;
        }
    }

    @Override
    public boolean getAllowFlight() {
        return getStatus() == PlayerStatus.JOINED && getBukkitPlayer() != null && getBukkitPlayer().getAllowFlight();
    }

    @Override
    public void setAllowFlight(boolean fly) {
        if (getStatus() != PlayerStatus.JOINED) return;
        if (getBukkitPlayer() == null) return;
        getBukkitPlayer().setAllowFlight(fly);
    }

    @Override
    public boolean isFlying() {
        return getStatus() == PlayerStatus.JOINED && getBukkitPlayer() != null && getBukkitPlayer().isFlying();
    }

    @Override
    public float getWalkSpeed() {
        if (getStatus() != PlayerStatus.JOINED) return 0;
        if (getBukkitPlayer() == null) return 0;
        return getBukkitPlayer().getWalkSpeed();
    }

    @Override
    public void setWalkSpeed(float speed) {
        if (getStatus() != PlayerStatus.JOINED) return;
        if (getBukkitPlayer() == null) return;
        getBukkitPlayer().setWalkSpeed(speed);
    }

    @Override
    public float getFlySpeed() {
        if (getStatus() != PlayerStatus.JOINED) return 0;
        if (getBukkitPlayer() == null) return 0;
        return getBukkitPlayer().getFlySpeed();
    }

    @Override
    public void setFlySpeed(float speed) {
        if (getStatus() != PlayerStatus.JOINED) return;
        if (getBukkitPlayer() == null) return;
        getBukkitPlayer().setFlySpeed(speed);
    }

    @Override
    public void setSneaking(boolean sneaking) {
        if (getStatus() != PlayerStatus.JOINED) return;
        if (getBukkitPlayer() == null) return;
        getBukkitPlayer().setSneaking(sneaking);
    }

    @Override
    public boolean isSneaking() {
        return getStatus() == PlayerStatus.JOINED && getBukkitPlayer() != null && getBukkitPlayer().isSneaking();
    }

    @Override
    public void setFlying(boolean fly) {
        if (getStatus() != PlayerStatus.JOINED) return;
        if (getBukkitPlayer() == null) return;
        getBukkitPlayer().setFlying(fly);
    }

    @Override
    public void kick(String reason) {
        if (getBukkitPlayer() == null) return;
        getBukkitPlayer().kickPlayer(reason);
    }

    @Override
    public void performCommand(String cmd) {
        if (getStatus() != PlayerStatus.JOINED) return;
        if (getBukkitPlayer() == null) return;
        getBukkitPlayer().performCommand(cmd);
    }

    @Override
    public int getTokens() {
        return Core.getMongoHandler().getCurrency(getUuid(), CurrencyType.TOKENS);
    }

    @Override
    public int getBalance() {
        return Core.getMongoHandler().getCurrency(getUuid(), CurrencyType.BALANCE);
    }

    @Override
    public void addTokens(int amount) {
        if (amount == 0) return;
        if (amount > 0) {
            getActionBar().show(ChatColor.YELLOW + "+" + CurrencyType.TOKENS.getIcon() + amount);
        } else {
            getActionBar().show(ChatColor.YELLOW + "-" + CurrencyType.TOKENS.getIcon() + amount);
        }
        Core.runTaskAsynchronously(() -> Core.getMongoHandler().changeAmount(getUuid(), amount, "plugin", CurrencyType.TOKENS, false));
    }

    @Override
    public void addBalance(int amount) {
        if (amount == 0) return;
        if (amount > 0) {
            getActionBar().show(ChatColor.GREEN + "+" + CurrencyType.BALANCE.getIcon() + amount);
        } else {
            getActionBar().show(ChatColor.GREEN + "-" + CurrencyType.BALANCE.getIcon() + amount);
        }
        Core.runTaskAsynchronously(() -> Core.getMongoHandler().changeAmount(getUuid(), amount, "plugin", CurrencyType.BALANCE, false));
    }

    @Override
    public void setTokens(int amount) {
        Core.runTaskAsynchronously(() -> Core.getMongoHandler().changeAmount(getUuid(), amount, "Core", CurrencyType.TOKENS, true));
    }

    @Override
    public void setBalance(int amount) {
        Core.runTaskAsynchronously(() -> Core.getMongoHandler().changeAmount(getUuid(), amount, "Core", CurrencyType.BALANCE, true));
    }

    @Override
    public void removeTokens(int amount) {
        if (amount == 0) return;
        if (amount > 0) {
            getActionBar().show(ChatColor.YELLOW + "-" + CurrencyType.TOKENS.getIcon() + amount);
        } else {
            getActionBar().show(ChatColor.YELLOW + "+" + CurrencyType.TOKENS.getIcon() + amount);
        }
        Core.runTaskAsynchronously(() -> Core.getMongoHandler().changeAmount(getUuid(), -amount, "Core", CurrencyType.TOKENS, false));
    }

    @Override
    public void removeBalance(int amount) {
        if (amount == 0) return;
        if (amount > 0) {
            getActionBar().show(ChatColor.GREEN + "-" + CurrencyType.BALANCE.getIcon() + amount);
        } else {
            getActionBar().show(ChatColor.GREEN + "+" + CurrencyType.BALANCE.getIcon() + amount);
        }
        Core.runTaskAsynchronously(() -> Core.getMongoHandler().changeAmount(getUuid(), -amount, "Core", CurrencyType.BALANCE, false));
    }

    @Override
    public void addStatistic(GameType gameType, StatisticType statisticType, int amount) {
        Core.getMongoHandler().addGameStat(gameType, statisticType, amount, this);
        new GameStatisticChangeEvent(this, gameType, statisticType, amount).call();
    }

    @Override
    public int getStatistic(GameType gameType, StatisticType statisticType) {
        return Core.getMongoHandler().getGameStat(gameType, statisticType, this);
    }

    @Override

    public Optional<InventoryView> getOpenInventory() {
        if (!getStatus().equals(PlayerStatus.JOINED)) return Optional.empty();
        return Optional.ofNullable(getBukkitPlayer().getOpenInventory());
    }

    @Override
    public Optional<Entity> getVehicle() {
        if (!getStatus().equals(PlayerStatus.JOINED)) return Optional.empty();
        return Optional.ofNullable(getBukkitPlayer().getVehicle());
    }

    @Override
    public void setLevel(int level) {
        if (!getStatus().equals(PlayerStatus.JOINED)) return;
        getBukkitPlayer().setLevel(level);
    }

    @Override
    public int getLevel() {
        return getStatus().equals(PlayerStatus.JOINED) ? getBukkitPlayer().getLevel() : 1;
    }

    @Override
    public Vector getVelocity() {
        if (!getStatus().equals(PlayerStatus.JOINED)) return new Vector();
        if (getBukkitPlayer() == null) return new Vector();
        return getBukkitPlayer().getVelocity();
    }

    @Override
    public void setVelocity(Vector vector) {
        if (!getStatus().equals(PlayerStatus.JOINED)) return;
        if (getBukkitPlayer() == null) return;
        getBukkitPlayer().setVelocity(vector);
    }

    @Override
    public void setExp(float exp) {
        if (!getStatus().equals(PlayerStatus.JOINED)) return;
        if (getBukkitPlayer() == null) return;
        getBukkitPlayer().setExp(exp);
    }

    @Override
    public float getExp() {
        if (!getStatus().equals(PlayerStatus.JOINED)) return 0;
        if (getBukkitPlayer() == null) return 0;
        return getBukkitPlayer().getExp();
    }

    @Override
    public void giveHonor(int amount) {
        honor += amount;
        Core.getHonorManager().displayHonor(this);
        getActionBar().show(ChatColor.LIGHT_PURPLE + "+" + amount + " Honor");
        Core.getMongoHandler().addHonor(getUuid(), amount);
    }

    @Override
    public void removeHonor(int amount) {
        honor -= amount;
        Core.getHonorManager().displayHonor(this);
        getActionBar().show(ChatColor.DARK_PURPLE + "-" + amount + " Honor");
        Core.getMongoHandler().addHonor(getUuid(), -amount);
    }

    @Override
    public void sendMap(MapView view) {
        if (view == null) return;
        if (!getStatus().equals(PlayerStatus.JOINED)) return;
        if (getBukkitPlayer() == null) return;
        getBukkitPlayer().sendMap(view);
    }

    @Override
    public void removePotionEffect(PotionEffectType type) {
        if (!getStatus().equals(PlayerStatus.JOINED)) return;
        if (getBukkitPlayer() == null) return;
        getBukkitPlayer().removePotionEffect(type);
    }

    @Override
    public boolean addPotionEffect(PotionEffect effect) {
        return getStatus().equals(PlayerStatus.JOINED) && getBukkitPlayer().addPotionEffect(effect);
    }

    @Override
    public boolean addPotionEffect(PotionEffect effect, boolean force) {
        return getStatus().equals(PlayerStatus.JOINED) && getBukkitPlayer().addPotionEffect(effect, force);
    }

    @Override
    public boolean hasPotionEffect(PotionEffectType type) {
        return getStatus().equals(PlayerStatus.JOINED) && getBukkitPlayer().hasPotionEffect(type);
    }

    @Override
    public boolean addPotionEffects(Collection<PotionEffect> effects) {
        return getStatus().equals(PlayerStatus.JOINED) && getBukkitPlayer().addPotionEffects(effects);
    }

    @Override
    public Collection<PotionEffect> getActivePotionEffects() {
        if (!getStatus().equals(PlayerStatus.JOINED)) return Collections.emptyList();
        if (getBukkitPlayer() == null) return Collections.emptyList();
        return getBukkitPlayer().getActivePotionEffects();
    }

    @Override
    public PotionEffect getPotionEffect(PotionEffectType type) {
        if (!getStatus().equals(PlayerStatus.JOINED)) return null;
        if (getBukkitPlayer() == null) return null;
        return getBukkitPlayer().getPotionEffect(type);
    }

    @Override
    public boolean isInsideVehicle() {
        return getStatus().equals(PlayerStatus.JOINED) && getBukkitPlayer() != null && getBukkitPlayer().isInsideVehicle();
    }

    @Override
    public boolean eject() {
        return getStatus().equals(PlayerStatus.JOINED) && getBukkitPlayer() != null && getBukkitPlayer().eject();
    }

    @Override
    public int getWindowId() {
        try {
            Object craftPlayer = Class.forName("org.bukkit.craftbukkit." + Core.getMinecraftVersion() +
                    ".entity.CraftPlayer").cast(getBukkitPlayer());
            Method m = craftPlayer.getClass().getDeclaredMethod("getHandle");
            Object entityPlayer = m.invoke(craftPlayer);
            Object entityHuman = Class.forName("net.minecraft.server." + Core.getMinecraftVersion() +
                    ".EntityHuman").cast(entityPlayer);
            Field field = entityHuman.getClass().getField("activeContainer");
            field.setAccessible(true);
            Object container = field.get(entityHuman);
            Field windowIdField = container.getClass().getField("windowId");
            return (int) windowIdField.get(container);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException |
                NoSuchFieldException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public boolean isInVehicle() {
        return getStatus().equals(PlayerStatus.JOINED) && getBukkitPlayer() != null && getBukkitPlayer().isInsideVehicle();
    }

    @Override
    public boolean leaveVehicle() {
        return getStatus().equals(PlayerStatus.JOINED) && getBukkitPlayer() != null && getBukkitPlayer().leaveVehicle();
    }

    @Override
    public void setMetadata(String name, MetadataValue metadata) {
        if (!getStatus().equals(PlayerStatus.JOINED)) return;
        getBukkitPlayer().setMetadata(name, metadata);
    }

    @Override
    public List<MetadataValue> getMetadata(String name) {
        if (!getStatus().equals(PlayerStatus.JOINED)) return new ArrayList<>();
        return getBukkitPlayer().getMetadata(name);
    }

    @Override
    public void removeMetadata(String name, Plugin plugin) {
        if (!getStatus().equals(PlayerStatus.JOINED)) return;
        getBukkitPlayer().removeMetadata(name, plugin);
    }

    @Override
    public long getOnlineTime() {
        return System.currentTimeMillis() - joinTime;
    }

    @Override
    public void setAchievementManager(CPlayerAchievementManager manager) {
        this.achievementManager = manager;
        for (Integer i : new ArrayList<>(queuedAchievements)) {
            achievementManager.giveAchievement(i);
        }
        queuedAchievements.clear();
    }
}

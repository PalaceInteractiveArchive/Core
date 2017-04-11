package network.palace.core.player.impl;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import lombok.Getter;
import lombok.Setter;
import network.palace.core.Core;
import network.palace.core.config.LanguageManager;
import network.palace.core.packets.AbstractPacket;
import network.palace.core.player.*;
import network.palace.core.player.impl.managers.*;
import network.palace.core.plugin.Plugin;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

/**
 * The type Core player.
 */
public class CorePlayer implements CPlayer {

    @Getter private final UUID uuid;
    private final String name;
    @Getter @Setter private Rank rank = Rank.SETTLER;
    @Getter @Setter private String locale = "en_US";
    @Getter @Setter private PlayerStatus status = PlayerStatus.LOGIN;
    @Getter private CPlayerAchievementManager achievement;
    @Getter private CPlayerActionBarManager actionBar = new CorePlayerActionBarManager(this);
    @Getter private CPlayerBossBarManager bossBar = new CorePlayerBossBarManager(this);
    @Getter private CPlayerHeaderFooterManager headerFooter = new CorePlayerHeaderFooterManager(this);
    @Getter private CPlayerScoreboardManager scoreboard = new CorePlayerScoreboardManager(this);
    @Getter private CPlayerTitleManager title = new CorePlayerTitleManager(this);
    @Getter private CPlayerParticlesManager particles = new CorePlayerParticlesManager(this);
    @Getter private CPlayerResourcePackManager resourcePack = new CorePlayerResourcePackManager(this);
    @Getter @Setter private String textureValue = "";
    @Getter @Setter private String textureSignature = "";
    @Getter @Setter private String pack = "none";

    /**
     * Instantiates a new Core player.
     *
     * @param uuid the uuid
     * @param name the name
     * @param rank the rank
     * @param ids  achievement list
     */
    public CorePlayer(UUID uuid, String name, Rank rank, List<Integer> ids) {
        this.uuid = uuid;
        this.name = name;
        this.rank = rank;
        this.achievement = new CorePlayerAchievementManager(this, ids);
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
    public void playSound(Location location, Sound sound, float volume, float pitch) {
        if (getStatus() != PlayerStatus.JOINED) return;
        if (getBukkitPlayer() == null) return;
        if (location == null) return;
        if (sound == null) return;
        getBukkitPlayer().playSound(location, sound, volume, pitch);
    }

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
    public void sendMessage(String message) {
        if (getStatus() != PlayerStatus.JOINED) return;
        if (getBukkitPlayer() == null) return;
        if (message == null) return;
        getBukkitPlayer().sendMessage(message);
    }

    @Override
    public void sendFormatMessage(JavaPlugin plugin, String key) {
        if (getStatus() != PlayerStatus.JOINED) return;
        if (getBukkitPlayer() == null) return;
        LanguageManager languageManager = null;
        if (plugin instanceof Core) {
            languageManager = Core.getLanguageFormatter();
        } else if (plugin instanceof Plugin) {
            languageManager = ((Plugin) plugin).getLanguageManager();
        }
        if (languageManager == null) {
            plugin.getLogger().log(Level.SEVERE, "PROBLEM GETTING LANGUAGE FORMATTER for key: " + key);
            return;
        }
        String message = languageManager.getFormat(getLocale(), key);
        if (message.equals("")) {
            plugin.getLogger().log(Level.SEVERE, "MESSAGE NULL for key: " + key);
            return;
        }
        getBukkitPlayer().sendMessage(message);
    }

    @Override
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
    public void respawn() {
        if (getStatus() != PlayerStatus.JOINED) return;
        if (getBukkitPlayer() == null) return;
        if (getBukkitPlayer().spigot() == null) return;
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
        return getStatus() == PlayerStatus.JOINED && getBukkitPlayer() != null && achievement.hasAchievement(i);
    }

    @Override
    public void giveAchievement(int i) {
        if (getStatus() != PlayerStatus.JOINED) return;
        if (getBukkitPlayer() == null) return;
        achievement.giveAchievement(i);
    }

    @Override
    public Block getTargetBlock(int range) {
        if (getBukkitPlayer() == null) return null;
        return getBukkitPlayer().getTargetBlock((Set<Material>) null, range);
    }

    @Override
    public int getPing() {
        if (getStatus() != PlayerStatus.JOINED) return 0;
        if (getBukkitPlayer() == null) return 0;
        try {
            Object craftPlayer = Class.forName("org.bukkit.craftbukkit.v" + Core.getInstance().getMcVersion() +
                    ".entity.CraftPlayer").cast(getBukkitPlayer());
            Method m = craftPlayer.getClass().getDeclaredMethod("getHandle");
            Object entityPlayer = m.invoke(craftPlayer);
            Field ping = entityPlayer.getClass().getDeclaredField("ping");
            ping.setAccessible(true);
            return (int) ping.get(entityPlayer);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException |
                NoSuchFieldException e) {
            e.printStackTrace();
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
}

package network.palace.core;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketListener;
import network.palace.core.achievements.AchievementManager;
import network.palace.core.command.CommandRegister;
import network.palace.core.config.LanguageManager;
import network.palace.core.config.YAMLConfigurationFile;
import network.palace.core.economy.EconomyManager;
import network.palace.core.library.LibraryHandler;
import network.palace.core.listener.ListenerRegister;
import network.palace.core.npc.EntityIDManager;
import network.palace.core.packets.adapters.SettingsAdapter;
import network.palace.core.permissions.PermissionManager;
import network.palace.core.player.CPlayerManager;
import network.palace.core.player.impl.CorePlayerWorldDownloadProtect;
import network.palace.core.player.impl.managers.CorePlayerManager;
import network.palace.core.plugin.PluginInfo;
import network.palace.core.resource.ResourceManager;
import network.palace.core.utils.SqlUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * This will manage all Modules and also the Core Managers.
 * <p>
 * You can access instances of other modules by depending on Core in your pom.xml, and then executing Core.get
 */
@PluginInfo(name = "Core", version = "1.6.5", depend = {"ProtocolLib"})
public class Core extends JavaPlugin {

    private boolean starting = true;

    private YAMLConfigurationFile configFile;
    private String serverType = "Hub";
    private String instanceName = "";
    private boolean debug = false;
    private boolean dashboardAndSqlDisabled = false;

    private LanguageManager languageManager;
    private CPlayerManager playerManager;
    private PermissionManager permissionManager;
    private EconomyManager economyManager;
    private ResourceManager resourceManager;
    private AchievementManager achievementManager;
    private EntityIDManager entityIDManger;

    private SqlUtil sqlUtil;

    @Override
    public final void onEnable() {
        // Kick all players on reload
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.kickPlayer(ChatColor.RED + "Server is reloading!");
        }
        // Load need libraries for Core
        LibraryHandler.loadLibraries(this);
        // Configurations
        configFile = new YAMLConfigurationFile(this, "config.yml");
        // Get info from config
        serverType = getCoreConfig().getString("server-type", "Unknown");
        instanceName = getCoreConfig().getString("instance-name", "ServerName");
        debug = getCoreConfig().getBoolean("debug", false);
        dashboardAndSqlDisabled = getCoreConfig().getBoolean("dashboardAndSqlDisabled", false);
        // Language Manager
        languageManager = new LanguageManager(this);
        // Settings adapter for player locales
        addPacketListener(new SettingsAdapter());
        // Register plugin channel
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        getServer().getMessenger().registerIncomingPluginChannel(this, "WDL|INIT", new CorePlayerWorldDownloadProtect());
        // SQL Classes
        sqlUtil = new SqlUtil();
        // Managers
        playerManager = new CorePlayerManager();
        permissionManager = new PermissionManager();
        resourceManager = new ResourceManager();
        economyManager = new EconomyManager();
        achievementManager = new AchievementManager();
        entityIDManger = new EntityIDManager();
        // Register Listeners
        new ListenerRegister(this);
        // Register Commands
        new CommandRegister(this);
        // Log
        logMessage("Core", ChatColor.DARK_GREEN + "Enabled");
        // Set starting to false after 5 to allow connecting
        runTaskLater(() -> setStarting(false), 100L);
    }

    @Override
    public final void onDisable() {
        logMessage("Core", ChatColor.DARK_RED + "Disabled");
    }

    /**
     * Gets core instance.
     *
     * @return Core instance
     */
    public static Core getInstance() {
        return Core.getPlugin(Core.class);
    }

    /**
     * Is debug enabled.
     *
     * @return the boolean
     */
    public static boolean isDebug() {
        return getInstance().debug;
    }

    /**
     * Is dashboard and sql disabled.
     *
     * @return the boolean
     */
    public static boolean isDashboardAndSqlDisabled() {
        return getInstance().dashboardAndSqlDisabled;
    }

    /**
     * Is core starting.
     *
     * @return the boolean
     */
    public static boolean isStarting() {
        return getInstance().starting;
    }

    /**
     * Sets starting.
     *
     * @param isStarting the is starting
     */
    public static void setStarting(boolean isStarting) {
        if (!isStarting) {
            logMessage("Core", ChatColor.DARK_GREEN + "Server Joinable!");
        } else {
            logMessage("Core", ChatColor.DARK_RED + "Server Not Joinable!");
        }
        getInstance().starting = isStarting;
    }

    /**
     * Gets server type.
     *
     * @return the server type
     */
    public static String getServerType() {
        return getInstance().serverType;
    }

    /**
     * Gets instance name.
     *
     * @return the instance name
     */
    public static String getInstanceName() {
        return getInstance().instanceName;
    }

    /**
     * Gets core version.
     *
     * @return Core version
     */
    public static String getVersion() {
        return getInstance().getDescription().getVersion();
    }

    /**
     * Gets player manager.
     *
     * @return the player manager
     */
    public static CPlayerManager getPlayerManager() {
        return getInstance().playerManager;
    }

    /**
     * Gets language formatter.
     *
     * @return the language formatter
     */
    public static LanguageManager getLanguageFormatter() {
        return getInstance().languageManager;
    }

    /**
     * Gets permission manager.
     *
     * @return the permission manager
     */
    public static PermissionManager getPermissionManager() {
        return getInstance().permissionManager;
    }

    /**
     * Gets economy.
     *
     * @return the economy
     */
    public static EconomyManager getEconomy() {
        return getInstance().economyManager;
    }

    /**
     * Gets resource manager.
     *
     * @return the resource manager
     */
    public static ResourceManager getResourceManager() {
        return getInstance().resourceManager;
    }

    /**
     * Gets achievement manager.
     *
     * @return the achievement manager
     */
    public static AchievementManager getAchievementManager() {
        return getInstance().achievementManager;
    }

    /**
     * Gets entity id manager.
     *
     * @return the entity id manager
     */
    public static EntityIDManager getEntityIDManager() {
        return getInstance().entityIDManger;
    }

    /**
     * Gets sql util.
     *
     * @return the sql util
     */
    public static SqlUtil getSqlUtil() {
        return getInstance().sqlUtil;
    }

    /**
     * Gets core config.
     *
     * @return the core config
     */
    public static FileConfiguration getCoreConfig() {
        return getInstance().configFile.getConfig();
    }

    /**
     * Bukkit Utils
     *
     * @param size  inventory size
     * @param title inventory title
     * @return the inventory
     */
    public static Inventory createInventory(int size, String title) {
        return Bukkit.createInventory(null, size, title);
    }

    /**
     * Gets bukkit world from name.
     *
     * @param name the name
     * @return the world
     */
    public static World getWorld(String name) {
        return Bukkit.getWorld(name);
    }

    /**
     * Gets the first/default bukkit world.
     *
     * @return the default world
     */
    public static World getDefaultWorld() {
        return Bukkit.getWorlds().get(0);
    }

    /**
     * Gets all worlds on the server.
     *
     * @return the worlds
     */
    public static List<World> getWorlds() {
        return Bukkit.getWorlds();
    }

    /**
     * Shutdown.
     */
    public static void shutdown() {
        Bukkit.shutdown();
    }

    /**
     * Register listener.
     *
     * @param listener the listener
     * @param plugin the plugin
     */
    public static void registerListener(Listener listener, JavaPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(listener, plugin);
    }

    /**
     * Call event.
     *
     * @param event the event
     */
    public static void callEvent(Event event) {
        Bukkit.getPluginManager().callEvent(event);
    }

    /**
     * Cancel task.
     *
     * @param taskId the task id
     */
    public static void cancelTask(int taskId) {
        Bukkit.getScheduler().cancelTask(taskId);
    }

    /**
     * Call sync method
     *
     * @param callable the callable
     * @return future
     */
    public static <T> Future<T> callSyncMethod(Callable<T> callable) {
        return Bukkit.getScheduler().callSyncMethod(getInstance(), callable);
    }

    /**
     * Run task asynchronously int.
     *
     * @param task the task
     * @return the task id
     */
    public static int runTaskAsynchronously(Runnable task) {
        return Bukkit.getScheduler().runTaskAsynchronously(getInstance(), task).getTaskId();
    }

    /**
     * Run task later async int.
     *
     * @param task  the task
     * @param delay the delay
     * @return the task id
     */
    public static int runTaskLaterAsynchronously(Runnable task, long delay) {
        return Bukkit.getScheduler().runTaskLaterAsynchronously(getInstance(), task, delay).getTaskId();
    }

    /**
     * Run task later int.
     *
     * @param task  the task
     * @param delay the delay
     * @return the task id
     */
    public static int runTaskLater(Runnable task, long delay) {
        return Bukkit.getScheduler().runTaskLater(getInstance(), task, delay).getTaskId();
    }

    /**
     * Run task timer asynchronously int.
     *
     * @param task   the task
     * @param delay  the delay
     * @param period the period
     * @return the task id
     */
    public static int runTaskTimerAsynchronously(Runnable task, long delay, long period) {
        return Bukkit.getScheduler().runTaskTimerAsynchronously(getInstance(), task, delay, period).getTaskId();
    }

    /**
     * Run task timer int.
     *
     * @param task   the task
     * @param delay  the delay
     * @param period the period
     * @return the task id
     */
    public static int runTaskTimer(Runnable task, long delay, long period) {
        return Bukkit.getScheduler().runTaskTimer(getInstance(), task, delay, period).getTaskId();
    }

    /**
     * Run task int.
     *
     * @param task the task
     * @return the task id
     */
    public static int runTask(Runnable task) {
        return Bukkit.getScheduler().runTask(getInstance(), task).getTaskId();
    }

    /**
     * Log message.
     *
     * @param name    the name
     * @param message the message
     */
    public static void logMessage(String name, String message) {
        logInfo(ChatColor.GOLD + name + ChatColor.DARK_GRAY + " > " + message);
    }

    /**
     * Debug log.
     *
     * @param s the s
     */
    public static void debugLog(String s) {
        if (isDebug()) {
            logMessage("CORE-DEBUG", s);
        }
    }

    /**
     * Log info.
     *
     * @param message the message
     */
    public static void logInfo(String message) {
        Bukkit.getConsoleSender().sendMessage(message);
    }

    /**
     * Add protocol lib packet listener.
     *
     * @param listener the listener
     */
    public static void addPacketListener(PacketListener listener) {
        ProtocolLibrary.getProtocolManager().addPacketListener(listener);
    }
}

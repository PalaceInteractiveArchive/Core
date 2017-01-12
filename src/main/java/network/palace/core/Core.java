package network.palace.core;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketListener;
import network.palace.core.command.CoreCommand;
import network.palace.core.command.CoreCommandMap;
import network.palace.core.commands.*;
import network.palace.core.commands.disabled.MeCommand;
import network.palace.core.commands.disabled.PrefixCommandListener;
import network.palace.core.config.LanguageManager;
import network.palace.core.config.YAMLConfigurationFile;
import network.palace.core.dashboard.DashboardConnection;
import network.palace.core.economy.EconomyManager;
import network.palace.core.library.LibraryHandler;
import network.palace.core.packets.adapters.SettingsAdapter;
import network.palace.core.permissions.PermissionManager;
import network.palace.core.player.CPlayerManager;
import network.palace.core.player.impl.CorePlayerWorldDownloadProtect;
import network.palace.core.player.impl.managers.CorePlayerManager;
import network.palace.core.plugin.PluginInfo;
import network.palace.core.resource.ResourceManager;
import network.palace.core.utils.ItemUtil;
import network.palace.core.utils.SqlUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

/**
 * This will manage all Modules and also the Core Managers.
 * <p>
 * You can access instances of other modules by depending on Core in your pom.xml, and then executing Core.get
 */
@PluginInfo(name = "Core", version = "1.5.1", depend = {"ProtocolLib"}, canReload = false)
public class Core extends JavaPlugin {

    private boolean starting = true;

    private YAMLConfigurationFile configFile;
    private String serverType = "Hub";
    private String instanceName = "";
    private boolean debug = false;

    private DashboardConnection dashboardConnection;

    private LanguageManager languageManager;
    private CPlayerManager playerManager;
    private PermissionManager permissionManager;
    private EconomyManager economyManager;
    private ResourceManager resourceManager;

    private CoreCommandMap commandMap;
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
        // Core command map
        commandMap = new CoreCommandMap(this);
        // Dashboard
        dashboardConnection = new DashboardConnection();
        // Register Listeners
        registerListeners();
        // Register Commands
        registerCommands();
        registerDisabledCommands();
        // Log
        logMessage("Core", ChatColor.DARK_GREEN + "Enabled");
        // Set starting to false after 5 to allow connecting
        Bukkit.getScheduler().runTaskLater(this, () -> starting = false, 100L);
    }

    /**
     * Register listeners.
     */
    public void registerListeners() {
        registerListener(new ItemUtil());
        registerListener(new PrefixCommandListener());
    }

    /**
     * Register disabled commands.
     */
    public void registerDisabledCommands() {
        registerCommand(new MeCommand());
    }

    /**
     * Register commands.
     */
    public void registerCommands() {
        registerCommand(new BalanceCommand());
        registerCommand(new HelpopCommand());
        registerCommand(new ListCommand());
        registerCommand(new PermCommand());
        registerCommand(new PluginsCommand());
        registerCommand(new ReloadCommand());
        registerCommand(new SafestopCommand());
        registerCommand(new TokenCommand());
    }

    /**
     * Register a core command.
     *
     * @param command the command
     */
    public final void registerCommand(CoreCommand command) {
        commandMap.registerCommand(command);
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
     * Gets dashboard connection.
     *
     * @return the dashboard connection
     */
    public static DashboardConnection getDashboardConnection() {
        return getInstance().dashboardConnection;
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
        return Bukkit.getServer().getWorld(name);
    }

    /**
     * Gets the first/default bukkit world.
     *
     * @return the default world
     */
    public static World getDefaultWorld() {
        return Bukkit.getServer().getWorlds().get(0);
    }

    /**
     * Gets all worlds on the server.
     *
     * @return the worlds
     */
    public static List<World> getWorlds() {
        return Bukkit.getServer().getWorlds();
    }

    /**
     * Shutdown.
     */
    public static void shutdown() {
        Bukkit.getServer().shutdown();
    }

    /**
     * Register listener.
     *
     * @param listener the listener
     */
    public static void registerListener(Listener listener) {
        Bukkit.getServer().getPluginManager().registerEvents(listener, getInstance());
    }

    /**
     * Cancel task.
     *
     * @param taskId the task id
     */
    public static void cancelTask(int taskId) {
        Bukkit.getServer().getScheduler().cancelTask(taskId);
    }

    /**
     * Run task later int.
     *
     * @param task  the task
     * @param delay the delay
     * @return the int
     */
    public static int runTaskLater(Runnable task, long delay) {
        return Bukkit.getServer().getScheduler().runTaskLater(getInstance(), task, delay).getTaskId();
    }

    /**
     * Run task timer int.
     *
     * @param task   the task
     * @param delay  the delay
     * @param period the period
     * @return the int
     */
    public static int runTaskTimer(Runnable task, long delay, long period) {
        return Bukkit.getServer().getScheduler().runTaskTimer(getInstance(), task, delay, period).getTaskId();
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
        Bukkit.getServer().getConsoleSender().sendMessage(message);
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

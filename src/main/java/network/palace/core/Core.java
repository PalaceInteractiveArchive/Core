package network.palace.core;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketListener;
import com.comphenix.protocol.utility.MinecraftVersion;
import lombok.Getter;
import lombok.Setter;
import network.palace.core.achievements.AchievementManager;
import network.palace.core.command.CoreCommand;
import network.palace.core.command.CoreCommandMap;
import network.palace.core.commands.*;
import network.palace.core.commands.disabled.MeCommand;
import network.palace.core.commands.disabled.PrefixCommandListener;
import network.palace.core.commands.disabled.StopCommand;
import network.palace.core.config.LanguageManager;
import network.palace.core.config.YAMLConfigurationFile;
import network.palace.core.crafting.CraftingMenu;
import network.palace.core.dashboard.DashboardConnection;
import network.palace.core.economy.EconomyManager;
import network.palace.core.errors.EnvironmentType;
import network.palace.core.errors.RollbarHandler;
import network.palace.core.honor.HonorManager;
import network.palace.core.library.LibraryHandler;
import network.palace.core.npc.SoftNPCManager;
import network.palace.core.packets.adapters.PlayerInfoAdapter;
import network.palace.core.packets.adapters.SettingsAdapter;
import network.palace.core.permissions.PermissionManager;
import network.palace.core.player.CPlayer;
import network.palace.core.player.CPlayerManager;
import network.palace.core.player.impl.CorePlayerWorldDownloadProtect;
import network.palace.core.player.impl.managers.CorePlayerManager;
import network.palace.core.plugin.PluginInfo;
import network.palace.core.resource.ResourceManager;
import network.palace.core.utils.Callback;
import network.palace.core.utils.ItemUtil;
import network.palace.core.utils.SqlUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * This will manage all Modules and also the Core Managers.
 * <p>
 * You can access instances of other modules by depending on Core in your pom.xml, and then executing Core.get
 */
@PluginInfo(name = "Core", version = "2.0.5", depend = {"ProtocolLib"})
public class Core extends JavaPlugin {

    private boolean starting = true;
    @Getter private final long startTime = System.currentTimeMillis();
    private YAMLConfigurationFile configFile;
    private String serverType = "Hub";
    private String instanceName = "";
    private boolean debug = false;
    private boolean dashboardAndSqlDisabled = false;
    @Getter private String mcVersion = Bukkit.getBukkitVersion();
    private boolean gameMode = false;

    @Getter private boolean showTitleOnLogin = false;
    @Getter private String loginTitle = "";
    @Getter private String loginSubTitle = "";

    @Getter private int loginTitleFadeIn = 10;
    @Getter private int loginTitleStay = 10;
    @Getter private int loginTitleFadeOut = 10;

    @Getter @Setter private String tabHeader = ChatColor.GOLD + "Palace Network - A Family of Servers";
    @Getter @Setter private String tabFooter = ChatColor.LIGHT_PURPLE + "You're on the " + ChatColor.GREEN + "Hub " + ChatColor.LIGHT_PURPLE + "server";

    private SqlUtil sqlUtil;
    private LanguageManager languageManager;
    private PermissionManager permissionManager;
    private EconomyManager economyManager;
    private ResourceManager resourceManager;
    private AchievementManager achievementManager;
    private SoftNPCManager softNPCManager;
    private CPlayerManager playerManager;
    private CoreCommandMap commandMap;
    private DashboardConnection dashboardConnection;
    private HonorManager honorManager;
    private CraftingMenu craftingMenu;

    @Getter private RollbarHandler rollbarHandler;

    @Getter private List<UUID> disabledPlayers = new ArrayList<>();

    @Getter private boolean isMinecraftGreaterOrEqualTo11_2 = MinecraftVersion.getCurrentVersion().getMinor() >= 12;

    @Override
    public final void onEnable() {
        // Kick all players on reload
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.kickPlayer(ChatColor.RED + "Server is reloading!");
        }
        // Load needed libraries for Core
        LibraryHandler.loadLibraries(this);
        // Configurations
        configFile = new YAMLConfigurationFile(this, "config.yml");
        rollbarHandler = new RollbarHandler(getCoreConfig().getString("accessToken", ""), EnvironmentType.fromString(getCoreConfig().getString("environment", "local")));
        rollbarHandler.watch();
        // Get info from config
        serverType = getCoreConfig().getString("server-type", "Unknown");
        instanceName = getCoreConfig().getString("instance-name", "ServerName");
        debug = getCoreConfig().getBoolean("debug", false);
        dashboardAndSqlDisabled = getCoreConfig().getBoolean("dashboardAndSqlDisabled", false);
        gameMode = getCoreConfig().getBoolean("isGameMode", false);
        if (getCoreConfig().getConfigurationSection("tab") != null) {
            setTabHeader(ChatColor.translateAlternateColorCodes('&', getCoreConfig().getString("tab.header")));
            setTabFooter(ChatColor.translateAlternateColorCodes('&', getCoreConfig().getString("tab.footer")));
        }
        showTitleOnLogin = getCoreConfig().getBoolean("showTitle", false);
        if (showTitleOnLogin) {
            loginTitle = getCoreConfig().getString("loginTitle", "");
            loginSubTitle = getCoreConfig().getString("loginSubTitle", "");

            loginTitleFadeIn = getCoreConfig().getInt("logFadeIn", 20);
            loginTitleStay = getCoreConfig().getInt("loginStay", 100);
            loginTitleFadeOut = getCoreConfig().getInt("loginFadeOut", 20);
        }
        // Settings adapter for player locales
        addPacketListener(new SettingsAdapter());
        // Player info adapter for player ping
        addPacketListener(new PlayerInfoAdapter());
        // Register plugin channel
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        getServer().getMessenger().registerIncomingPluginChannel(this, "WDL|INIT", new CorePlayerWorldDownloadProtect());
        // SQL Classes
        sqlUtil = new SqlUtil();
        // Managers
        languageManager = new LanguageManager();
        playerManager = new CorePlayerManager();
        permissionManager = new PermissionManager();
        resourceManager = new ResourceManager();
        economyManager = new EconomyManager();
        achievementManager = new AchievementManager();
        softNPCManager = new SoftNPCManager();
        // Setup the honor manager
        honorManager = new HonorManager();
        honorManager.provideMappings(sqlUtil.getHonorMappings());
        // Core command map
        commandMap = new CoreCommandMap(this);
        // Dashboard
        dashboardConnection = new DashboardConnection();
        // Crafting Menu
        craftingMenu = new CraftingMenu();
        // Register Listeners
        registerListeners();
        // Register Commands
        registerCommands();
        registerDisabledCommands();
        mcVersion = mcVersion.replace("-SNAPSHOT", "").replace("R0.", "R")
                .replace(".", "_").replaceAll("_[0-9]-R", "_R").replace("-", "_");
        // Log
        logMessage("Core", ChatColor.DARK_GREEN + "Enabled");

        // If we're running in game-mode, set starting to false immediately.
        // Otherwise, we'll wait the 7 seconds.
        if (isGameMode()) {
            logMessage("Core", ChatColor.BLUE + "" + ChatColor.BOLD + "Running in game mode, skipping startup phase!");
            setStarting(false);
        } else runTaskLater(() -> setStarting(false), 20 * 7);
        Bukkit.getServer().clearRecipes();
    }

    /**
     * Register listeners.
     */
    private void registerListeners() {
        registerListener(new ItemUtil());
        registerListener(new PrefixCommandListener());
        registerListener(craftingMenu);
    }

    /**
     * Register disabled commands.
     */
    private void registerDisabledCommands() {
        registerCommand(new MeCommand());
        registerCommand(new StopCommand());
    }

    /**
     * Register commands.
     */
    private void registerCommands() {
        registerCommand(new AchievementCommand());
        registerCommand(new BalanceCommand());
        registerCommand(new FlyCommand());
        registerCommand(new HelpopCommand());
        registerCommand(new HonorCommand());
        registerCommand(new ListCommand());
        registerCommand(new MyHonorCommand());
        registerCommand(new OnlineCommand());
        registerCommand(new PermCommand());
        registerCommand(new PingCommand());
        registerCommand(new PluginsCommand());
        registerCommand(new ReloadCommand());
        registerCommand(new ShutdownCommand());
        registerCommand(new SpawnCommand());
        registerCommand(new TokenCommand());
        registerCommand(new TopHonorCommand());
        runTask(() -> {
            boolean park = false;
            for (Plugin p : Bukkit.getPluginManager().getPlugins()) {
                if (p.getName().equals("ParkManager")) {
                    park = true;
                }
            }
            if (!park) registerCommand(new TeleportCommand());
        });
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
        if (!isStarting) logMessage("Core", ChatColor.DARK_GREEN + "Server Joinable!");
        else logMessage("Core", ChatColor.DARK_RED + "Server Not Joinable!");
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
     * Is this instance running in game-mode?
     * <p>
     * GameMode allows the server to skip the startup phase so it can start faster
     *
     * @return the game-mode status
     */
    public static boolean isGameMode() {
        return getInstance().gameMode;
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
     * Gets command map.
     *
     * @return the command map
     */
    public static CoreCommandMap getCommandMap() {
        return getInstance().commandMap;
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
     * Gets honor manager
     *
     * @return the honor manager
     */
    public static HonorManager getHonorManager() {
        return getInstance().honorManager;
    }

    /**
     * Gets crafting menu
     *
     * @return the crafting menu
     */
    public static CraftingMenu getCraftingMenu() {
        return getInstance().craftingMenu;
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
     * Gets soft npc manager.
     *
     * @return the soft npc manager
     */
    public static SoftNPCManager getSoftNPCManager() {
        return getInstance().softNPCManager;
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
     * @return the config core is using
     */
    public static FileConfiguration getCoreConfig() {
        return getInstance().configFile.getConfig();
    }

    /**
     * Create a new inventory
     *
     * @param size  the size of the inventory
     * @param title the name of the inventory
     * @return the new inventory
     */
    public static Inventory createInventory(int size, String title) {
        return Bukkit.createInventory(null, size, title);
    }

    /**
     * Gets Bukkit world from name.
     *
     * @param name the name of the world
     * @return the world
     */
    public static World getWorld(String name) {
        return Bukkit.getWorld(name);
    }

    /**
     * The main world
     *
     * @return the default world
     */
    public static World getDefaultWorld() {
        return Bukkit.getWorlds().get(0);
    }

    /**
     * Gets all worlds on the server.
     *
     * @return the current registered worlds
     */
    public static List<World> getWorlds() {
        return Bukkit.getWorlds();
    }

    /**
     * Shutdown the server.
     */
    public static void shutdown() {
        Bukkit.shutdown();
    }

    /**
     * Register listener.
     *
     * @param listener the listener to be registered
     */
    public static void registerListener(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, getInstance());
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
     * @param s the message to log
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

    /**
     * Send all players to another server
     *
     * @param server   the server to send to
     * @param callback the callbacks for the actions
     */
    public static void sendAllPlayers(String server, Callback callback) {
        for (CPlayer player : Core.getPlayerManager().getOnlinePlayers()) {
            player.sendToServer(server);
        }
        Core.runTaskLater(callback::finished, 40L);
    }
}

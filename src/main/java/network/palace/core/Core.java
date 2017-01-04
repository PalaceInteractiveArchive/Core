package network.palace.core;

import com.comphenix.protocol.ProtocolLibrary;
import lombok.Getter;
import lombok.Setter;
import network.palace.core.command.CoreCommand;
import network.palace.core.command.CoreCommandMap;
import network.palace.core.commands.*;
import network.palace.core.config.LanguageFormatter;
import network.palace.core.config.YAMLConfigurationFile;
import network.palace.core.dashboard.DashboardConnection;
import network.palace.core.economy.Economy;
import network.palace.core.library.LibraryHandler;
import network.palace.core.packets.adapters.SettingsAdapter;
import network.palace.core.permissions.PermissionManager;
import network.palace.core.player.CPlayerManager;
import network.palace.core.player.impl.CorePlayerManager;
import network.palace.core.player.impl.CorePlayerWorldDownloadProtect;
import network.palace.core.plugin.PluginInfo;
import network.palace.core.resource.ResourceManager;
import network.palace.core.utils.ItemUtil;
import network.palace.core.utils.SqlUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.List;

@PluginInfo(name = "Core")
public class Core extends JavaPlugin {
    @Getter @Setter private String serverType = "Hub";
    @Getter @Setter private String instanceName = "";
    @Getter @Setter private boolean testNetwork = false;
    @Getter @Setter private boolean debug = false;
    private CoreCommandMap commandMap;

    @Getter
    private DashboardConnection dashboardConnection;

    private LanguageFormatter languageFormatter;
    private CPlayerManager playerManager;
    private PermissionManager permissionManager;
    private Economy economy;
    private ResourceManager resourceManager;

    private SqlUtil sqlUtil;

    private YAMLConfigurationFile configFile;

    @Override
    public final void onEnable() {
        // Kick all players on reload
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.kickPlayer(ChatColor.RED + "Server is reloading!");
        }
        // Libraries
        LibraryHandler.loadLibraries(this);
        // Configurations
        configFile = new YAMLConfigurationFile(this, "", "config.yml");
        getConfig().getDefaults();
        setServerType(getCoreConfig().getString("server-type"));
        setInstanceName(getCoreConfig().getString("instance-name"));
        setTestNetwork(getCoreConfig().getBoolean("test-network"));
        setDebug(getCoreConfig().getBoolean("debug"));
        // Formatter
        languageFormatter = new LanguageFormatter(this);
        // Protocol lib adapters
        ProtocolLibrary.getProtocolManager().addPacketListener(new SettingsAdapter());
        // Register plugin channel
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        getServer().getMessenger().registerIncomingPluginChannel(this, "WDL|INIT", new CorePlayerWorldDownloadProtect());
        // SQL Classes
        sqlUtil = new SqlUtil();
        // Managers
        playerManager = new CorePlayerManager();
        permissionManager = new PermissionManager();
        resourceManager = new ResourceManager();
        economy = new Economy();
        commandMap = new CoreCommandMap(this);
        //Dashboard
        dashboardConnection = new DashboardConnection();
        // Register Listeners
        registerListeners();
        // Register Commands
        registerCommands();
        logMessage("Core", ChatColor.DARK_GREEN + "Enabled");
    }

    public void registerListeners() {
        registerListener(new ItemUtil());
    }

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

    public final void registerCommand(CoreCommand command) {
        commandMap.registerCommand(command);
    }

    @Override
    public final void onDisable() {
        logMessage("Core", ChatColor.DARK_RED + "Disabled");
    }

    /* Core Info */
    public static Core getInstance() {
        return Core.getPlugin(Core.class);
    }

    public static String getVersion() {
        return getInstance().getDescription().getVersion();
    }

    /* Managers */
    public static CPlayerManager getPlayerManager() {
        return getInstance().playerManager;
    }

    public static LanguageFormatter getLanguageFormatter() {
        return getInstance().languageFormatter;
    }

    public static PermissionManager getPermissionManager() {
        return getInstance().permissionManager;
    }

    public static Economy getEconomy() {
        return getInstance().economy;
    }

    public static ResourceManager getResourceManager() {
        return getInstance().resourceManager;
    }

    /* SQL Classes */
    public static SqlUtil getSqlUtil() {
        return getInstance().sqlUtil;
    }

    /* Configurations */
    public static FileConfiguration getCoreConfig() {
        return getInstance().configFile.getConfig();
    }

    /**
     * Bukkit Utils
     */
    @SuppressWarnings("unused")
    public static Inventory createInventory(int size, String title) {
        return Bukkit.createInventory(null, size, title);
    }

    @SuppressWarnings("unused")
    public static World getWorld(String name) {
        return getBukkitServer().getWorld(name);
    }

    public static World getDefaultWorld() {
        return getBukkitServer().getWorlds().get(0);
    }

    @SuppressWarnings("unused")
    public static List<World> getWorlds() {
        return getBukkitServer().getWorlds();
    }

    @SuppressWarnings("unused")
    public static void shutdown() {
        getBukkitServer().shutdown();
    }

    public static void registerListener(Listener listener) {
        getPluginManager().registerEvents(listener, getInstance());
    }

    @SuppressWarnings("unused")
    public static void cancelTask(int taskId) {
        getScheduler().cancelTask(taskId);
    }

    @SuppressWarnings("UnusedReturnValue")
    public static int runTaskLater(Runnable task, long delay) {
        return getScheduler().runTaskLater(getInstance(), task, delay).getTaskId();
    }

    @SuppressWarnings("UnusedReturnValue")
    public static int runTaskTimer(Runnable task, long delay, long period) {
        return getScheduler().runTaskTimer(getInstance(), task, delay, period).getTaskId();
    }

    /* Log Utils */
    public static void logMessage(String name, String message) {
        logInfo(ChatColor.GOLD + name + ChatColor.DARK_GRAY + " > " + message);
    }

    public static void debugLog(String s) {
        if (Core.getInstance().isDebug()) {
            logMessage("CORE-DEBUG", s);
        }
    }

    public static void logInfo(String message) {
        getConsoleSender().sendMessage(message);
    }

    /**
     * Bukkit Getters
     */
    private static PluginManager getPluginManager() {
        return getBukkitServer().getPluginManager();
    }

    private static ConsoleCommandSender getConsoleSender() {
        return getBukkitServer().getConsoleSender();
    }

    private static BukkitScheduler getScheduler() {
        return Bukkit.getScheduler();
    }

    private static Server getBukkitServer() {
        return Bukkit.getServer();
    }
}

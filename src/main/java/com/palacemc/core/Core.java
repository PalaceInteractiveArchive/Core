package com.palacemc.core;

import com.comphenix.protocol.ProtocolLibrary;
import com.palacemc.core.command.CoreCommand;
import com.palacemc.core.command.CoreCommandMap;
import com.palacemc.core.commands.ListCommand;
import com.palacemc.core.commands.PluginsCommand;
import com.palacemc.core.commands.ReloadCommand;
import com.palacemc.core.commands.SafestopCommand;
import com.palacemc.core.config.LanguageFormatter;
import com.palacemc.core.config.YAMLConfigurationFile;
import com.palacemc.core.dashboard.DashboardConnection;
import com.palacemc.core.library.LibraryHandler;
import com.palacemc.core.packets.adapters.SettingsAdapter;
import com.palacemc.core.player.CPlayerManager;
import com.palacemc.core.player.impl.CorePlayerManager;
import com.palacemc.core.plugin.PluginInfo;
import com.palacemc.core.utils.ItemUtil;
import com.palacemc.core.utils.SqlUtil;
import lombok.Getter;
import lombok.Setter;
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

import java.net.URISyntaxException;
import java.util.List;

@PluginInfo(name = "Core")
public class Core extends JavaPlugin {
    @Getter @Setter private static String serverType = "Hub";
    @Getter @Setter private static String instanceName = "";
    @Getter @Setter private static boolean testNetwork = false;
    private CoreCommandMap commandMap;

    @Getter private static DashboardConnection dashboardConnection;

    private LanguageFormatter languageFormatter;
    private CPlayerManager playerManager;

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
        // Formatter
        languageFormatter = new LanguageFormatter(this);
        // Protocol lib adapters
        ProtocolLibrary.getProtocolManager().addPacketListener(new SettingsAdapter());
        // Register plugin channel
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        // Managers
        playerManager = new CorePlayerManager();
        commandMap = new CoreCommandMap(this);
        // SQL Classes
        sqlUtil = new SqlUtil();
        // Configurations
        configFile = new YAMLConfigurationFile(this, "", "config.yml");
        getConfig().getDefaults();
        setServerType(getCoreConfig().getString("server-type"));
        setInstanceName(getCoreConfig().getString("instance-name"));
        setTestNetwork(getCoreConfig().getBoolean("test-network"));
        //Dashboard
        try {
            dashboardConnection = new DashboardConnection();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
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
        registerCommand(new ListCommand());
        registerCommand(new PluginsCommand());
        registerCommand(new SafestopCommand());
        registerCommand(new ReloadCommand());
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

    /* Log Utils */
    public static void logMessage(String name, String message) {
        logInfo(ChatColor.GOLD + name + ChatColor.DARK_GRAY + " > " + message);
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

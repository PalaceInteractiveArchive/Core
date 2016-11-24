package com.thepalace.core;

import com.comphenix.protocol.ProtocolLibrary;
import com.thepalace.core.config.LanguageFormatter;
import com.thepalace.core.packets.adapters.SettingsAdapter;
import com.thepalace.core.player.CPlayerManager;
import com.thepalace.core.player.impl.CorePlayerManager;
import com.thepalace.core.plugin.Plugin;
import com.thepalace.core.library.LibraryHandler;
import com.thepalace.core.plugin.PluginInfo;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;
import java.util.List;

@PluginInfo(name = "Core")
public class Core extends JavaPlugin {

    private final List<Plugin> plugins = new ArrayList<>();

    private LanguageFormatter languageFormatter;
    private CPlayerManager playerManager;

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
        logMessage("Core", ChatColor.DARK_GREEN + "Enabled");
    }

    @Override
    public final void onDisable() {
        logMessage("Core", ChatColor.DARK_RED + "Disabled");
    }

    /* Plugin Utils */
    public static void onPluginEnable(Plugin plugin) {
        getInstance().plugins.add(plugin);
    }

    public static void onPluginDisable(Plugin plugin) {
        getInstance().plugins.remove(plugin);
    }

    /* Core Info */
    public static Core getInstance() {
        return Core.getPlugin(Core.class);
    }

    public static double getVersion() {
        PluginInfo annotation = getInstance().getClass().getAnnotation(PluginInfo.class);
        if (annotation != null) {
            return annotation.version();
        } else {
            return 1.0;
        }
    }

    /* Managers */
    public static CPlayerManager getPlayerManager() {
        return getInstance().playerManager;
    }

    public static LanguageFormatter getLanguageFormatter() {
        return getInstance().languageFormatter;
    }

    /** Bukkit Utils */
    @SuppressWarnings("unused")
    public static Inventory createInventory(int size, String title) {
        return Bukkit.createInventory(null, size, title);
    }

    @SuppressWarnings("unused")
    public static World getWorld(String name) {
        return getBukkitServer().getWorld(name);
    }

    @SuppressWarnings("unused")
    public static void shutdown() {
        getBukkitServer().shutdown();
    }

    public static void callEvent(Event event) {
        getPluginManager().callEvent(event);
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

    /** Bukkit Getters */
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

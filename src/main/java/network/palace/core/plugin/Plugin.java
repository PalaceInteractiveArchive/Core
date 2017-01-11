package network.palace.core.plugin;

import network.palace.core.Core;
import network.palace.core.command.CoreCommand;
import network.palace.core.command.CoreCommandMap;
import network.palace.core.config.LanguageManager;
import network.palace.core.library.LibraryHandler;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * The type Plugin.
 */
public class Plugin extends JavaPlugin {

    @Getter private PluginInfo info;
    @Getter private LanguageManager languageManager;
    @Getter private CoreCommandMap commandMap;

    @Override
    public final void onEnable() {
        try {
            // Start library downloading and loading
            LibraryHandler.loadLibraries(this);
            // Check if Core is enabled if not can't work
            if (!Core.getInstance().isEnabled()) return;
            // Get plugin info
            info = getClass().getAnnotation(PluginInfo.class);
            if (info == null) {
                throw new IllegalStateException("You must annotate your class with the @PluginInfo annotation!");
            }
            // Load languages
            languageManager = new LanguageManager(this);
            // Start command map
            commandMap = new CoreCommandMap(this);
            // Plugin enabled finally
            onPluginEnable();
            // Log enabled
            Core.logMessage(getInfo().name(), ChatColor.DARK_GREEN + "Plugin Enabled");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public final void onDisable() {
        try {
            onPluginDisable();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (getInfo() == null) return;
        Core.logMessage(getInfo().name(), ChatColor.DARK_RED + "Plugin Disabled");
    }

    /**
     * On plugin enable.
     *
     * @throws Exception the exception
     */
    protected void onPluginEnable() throws Exception { Core.logMessage(getInfo().name(), ChatColor.RED + "Did not run any code on enable!"); }

    /**
     * On plugin disable.
     *
     * @throws Exception the exception
     */
    protected void onPluginDisable() throws Exception {}

    /**
     * Register command.
     *
     * @param command the command
     */
    public void registerCommand(CoreCommand command) {
        getCommandMap().registerCommand(command);
    }

    /**
     * Register listener.
     *
     * @param listener the listener
     */
    public void registerListener(Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }

    /**
     * Run task timer int.
     *
     * @param runnable the runnable
     * @param delay    the delay
     * @param period   the period
     * @return the int
     */
    public int runTaskTimer(Runnable runnable, long delay, long period) {
        return Bukkit.getScheduler().runTaskTimer(this, runnable, delay, period).getTaskId();
    }

    /**
     * Run task timer bukkit int.
     *
     * @param runnable the runnable
     * @param delay    the delay
     * @param period   the period
     * @return the int
     */
    public int runTaskTimerBukkit(BukkitRunnable runnable, long delay, long period) {
        return runnable.runTaskTimer(this, delay, period).getTaskId();
    }

    /**
     * Run task later int.
     *
     * @param runnable the runnable
     * @param delay    the delay
     * @return the int
     */
    public int runTaskLater(Runnable runnable, long delay) {
        return Bukkit.getScheduler().runTaskLater(this, runnable, delay).getTaskId();
    }

    /**
     * Schedule sync delayed task int.
     *
     * @param runnable the runnable
     * @param delay    the delay
     * @return the int
     */
    public int scheduleSyncDelayedTask(Runnable runnable, long delay) {
        return Bukkit.getScheduler().scheduleSyncDelayedTask(this, runnable, delay);
    }

    /**
     * Schedule sync repeating task int.
     *
     * @param runnable the runnable
     * @param delay    the delay
     * @param period   the period
     * @return the int
     */
    public int scheduleSyncRepeatingTask(Runnable runnable, long delay, long period) {
        return Bukkit.getScheduler().scheduleSyncRepeatingTask(this, runnable, delay, period);
    }
}

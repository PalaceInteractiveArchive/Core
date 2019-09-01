package network.palace.core.plugin;

import lombok.Getter;
import network.palace.core.Core;
import network.palace.core.command.CoreCommand;
import network.palace.core.command.CoreCommandMap;
import network.palace.core.errors.ErrorLog;
import network.palace.core.errors.RollbarHandler;
import network.palace.core.library.LibraryHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * The base plugin used in Core plugins.
 */
public class Plugin extends JavaPlugin {

    @Getter private PluginInfo info;
    @Getter private ErrorLog errorLog;
    @Getter private CoreCommandMap commandMap;
    @Getter private RollbarHandler rollbarHandler;

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
            // Check rollbar info
//            errorLog = getClass().getAnnotation(ErrorLog.class);
//            if (errorLog != null) {
//                if (!errorLog.enabled()) return;
//                rollbarHandler = new RollbarHandler(errorLog.accessToken(), errorLog.environment());
//                rollbarHandler.watch();
//            }
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
     * Start the plugin up
     *
     * @throws Exception any error thrown during the plugin's startup phase
     */
    protected void onPluginEnable() throws Exception {
        Core.logMessage(getInfo().name(), ChatColor.RED + "Did not run any code on enable!");
    }

    /**
     * Shut the plugin down
     *
     * @throws Exception any error thrown during the plugin's shutdown phase
     */
    protected void onPluginDisable() throws Exception {
    }

    /**
     * Register command a Core command.
     *
     * @param command the command to be registered
     */
    public void registerCommand(CoreCommand command) {
        getCommandMap().registerCommand(command);
    }

    /**
     * Register a new listener
     *
     * @param listener the listener to be registered
     */
    public void registerListener(Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }

    /**
     * Run a task on an interval
     *
     * @param runnable the task to run
     * @param delay    the delay until execution starts
     * @param period   the interval to run the task on
     * @return the id of the task
     */
    public int runTaskTimer(Runnable runnable, long delay, long period) {
        return Bukkit.getScheduler().runTaskTimer(this, runnable, delay, period).getTaskId();
    }

    /**
     * Run a task on an interval
     *
     * @param runnable the task to run
     * @param delay    the delay until execution starts
     * @param period   the interval to run the task on.
     * @return the id of the task
     */
    public int runTaskTimerBukkit(BukkitRunnable runnable, long delay, long period) {
        return runnable.runTaskTimer(this, delay, period).getTaskId();
    }

    /**
     * Run a task after `delay` amount of ticks
     *
     * @param runnable the task to run
     * @param delay    the delay until execution will happen
     * @return the id of the task
     */
    public int runTaskLater(Runnable runnable, long delay) {
        return Bukkit.getScheduler().runTaskLater(this, runnable, delay).getTaskId();
    }

    /**
     * Run a task after `delay` amount of time
     *
     * @param runnable the task to run
     * @param delay    the time in ticks before starting execution
     * @return the id of the task
     */
    public int scheduleSyncDelayedTask(Runnable runnable, long delay) {
        return Bukkit.getScheduler().scheduleSyncDelayedTask(this, runnable, delay);
    }

    /**
     * Run a task every `period` ticks.
     *
     * @param runnable the task to run
     * @param delay    the delay before the first execution
     * @param period   the time between each execution
     * @return the id of the task
     */
    public int scheduleSyncRepeatingTask(Runnable runnable, long delay, long period) {
        return Bukkit.getScheduler().scheduleSyncRepeatingTask(this, runnable, delay, period);
    }
}

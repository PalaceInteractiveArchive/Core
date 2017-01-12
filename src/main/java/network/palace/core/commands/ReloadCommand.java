package network.palace.core.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;

/**
 * The type Reload command.
 */
@CommandMeta(aliases = {"rl"}, description = "Reload individual Core plugins.")
public class ReloadCommand extends CoreCommand {

    /**
     * Instantiates a new Reload command.
     */
    public ReloadCommand() {
        super("reload");
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "/reload [Plugin Name] <New Jar Name>");
            return;
        }
        String p = args[0];
        JavaPlugin plugin = (JavaPlugin) Bukkit.getPluginManager().getPlugin(p);
        if (plugin == null) {
            sender.sendMessage(ChatColor.RED + "That plugin doesn't exist!");
            return;
        }
        if (!(plugin instanceof network.palace.core.plugin.Plugin)) {
            sender.sendMessage(ChatColor.RED + "Only Core plugins can be reloaded!");
            return;
        }
        network.palace.core.plugin.Plugin cp = (network.palace.core.plugin.Plugin) plugin;
        if (!cp.getInfo().canReload()) {
            sender.sendMessage(ChatColor.RED + "This plugin doesn't support reloading!");
            return;
        }
        final String name;
        if (args.length == 2) {
            name = args[1];
        } else {
            name = plugin.getName();
        }
        JavaPluginLoader jpl = (JavaPluginLoader) plugin.getPluginLoader();
        jpl.disablePlugin(plugin);
        SimplePluginManager spm = (SimplePluginManager) Bukkit.getPluginManager();
        try {
            Field f = SimplePluginManager.class.getDeclaredField("plugins");
            f.setAccessible(true);
            List<Plugin> plugins = (List<Plugin>) f.get(spm);
            plugins.remove(plugin);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        System.gc();
        File f = new File("plugins/" + name + ".jar");
        if (!f.exists()) {
            sender.sendMessage(ChatColor.RED + name + ".jar doesn't exist!");
            return;
        }
        Plugin np;
        try {
            np = Bukkit.getPluginManager().loadPlugin(f);
        } catch (InvalidPluginException | InvalidDescriptionException e) {
            sender.sendMessage(ChatColor.RED + "There was an error loading the plugin!");
            e.printStackTrace();
            return;
        }
        Bukkit.getPluginManager().enablePlugin(np);
        sender.sendMessage(ChatColor.GREEN + np.getName() + " has been reloaded!");
    }
}

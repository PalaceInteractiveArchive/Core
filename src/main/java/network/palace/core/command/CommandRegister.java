package network.palace.core.command;

import network.palace.core.Core;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;

import java.util.Set;

public class CommandRegister {

    public CommandRegister(JavaPlugin plugin) {
        // Create command map for the plugin
        CoreCommandMap commandMap = new CoreCommandMap(plugin);
        // Create a reflections mapping for the plugin
        Reflections reflections = new Reflections(plugin.getClass().getPackage().getName());
        // Find all commands for the plugin
        Set<Class<? extends CoreCommand>> commands = reflections.getSubTypesOf(CoreCommand.class);
        for (Class<? extends CoreCommand> command : commands) {
            // Fixes help subcommand problem
            if (command.getName().startsWith(CoreCommand.class.getName() + "$")) continue;
            // Skip over any sub commands
            if (command.isAnnotationPresent(SubCommand.class)) continue;
            // Try to register the command
            try {
                CoreCommand coreCommand = command.newInstance();
                commandMap.registerCommand(coreCommand);
            } catch (InstantiationException | IllegalAccessException e) {
                Core.logMessage(plugin.getName(), ChatColor.DARK_RED + "Can not create command " + command.getSimpleName());
                e.printStackTrace();
            }
        }
    }
}

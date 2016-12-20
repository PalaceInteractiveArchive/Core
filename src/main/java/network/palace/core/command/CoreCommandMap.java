package network.palace.core.command;

import network.palace.core.Core;
import network.palace.core.plugin.Plugin;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;

/**
 * This is a command map for the plugin, and will most likely be modified to provide some level of more interesting functionality.
 */
public final class CoreCommandMap {

    @Getter private final Map<String, CoreCommand> topLevelCommands = new HashMap<>();
    private final JavaPlugin plugin;

    public CoreCommandMap(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Registers a command for handling.
     * @param command The command to register.
     */
    public void registerCommand(CoreCommand command) {
        // Check if we have the command registered using the same name
        if (topLevelCommands.containsKey(command.getName())) return; // Return if so
        PluginCommand command1 = getCommand(command.getName(), plugin); // Create a command for force registration
        command1.setExecutor(command); //Set the executor
        command1.setTabCompleter(command); //Tab completer
        CommandMeta annotation = command.getClass().getAnnotation(CommandMeta.class); // Get the commandMeta
        if (annotation != null){
            command1.setAliases(Arrays.asList(annotation.aliases()));
            command1.setDescription(annotation.description());
            command1.setUsage(annotation.usage());
        }
        // Remove old commands before register
        assert annotation != null;
        ArrayList<String> tempList = new ArrayList<>(Arrays.asList(annotation.aliases()));
        tempList.add(command.getName());
        for (String oldCommand : tempList) {
            removeKnownCommand("bukkit:" + oldCommand);
            removeKnownCommand(oldCommand);
        }
        getCommandMap().register(plugin.getDescription().getName(), command1); // Register it with Bukkit
        String pluginName = "Unknown";
        if (plugin instanceof Core) {
            pluginName = "Core";
        } else if (plugin instanceof Plugin) {
            pluginName = ((Plugin) plugin).getInfo().name();
        }
        Core.logMessage(pluginName, "Registered " + command.toString());
        this.topLevelCommands.put(command.getName(), command); // Put it in the hash map now that we've registered it.
    }

    /**
     * Creates a new instance of the command
     *
     * @return new PluginCommand instance of the requested command name
     */
    private PluginCommand getCommand(String name, org.bukkit.plugin.Plugin plugin) {
        PluginCommand command = null;
        try {
            Constructor<PluginCommand> commandConstructor = PluginCommand.class.getDeclaredConstructor(String.class, org.bukkit.plugin.Plugin.class);
            commandConstructor.setAccessible(true);
            command = commandConstructor.newInstance(name, plugin);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return command;
    }

    /**
     * Gets the command map from bukkit
     *
     * @return The command map from bukkit
     */
    private CommandMap getCommandMap() {
        CommandMap commandMap = null;
        try {
            PluginManager pluginManager = Bukkit.getPluginManager();
            Field commandMapField = pluginManager.getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            commandMap = (CommandMap) commandMapField.get(pluginManager);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return commandMap;
    }

    /**
     * Gets the known commands for the command map
     *
     * @return A hashmap of the known commands from the command map
     */
    @SuppressWarnings("unchecked")
    private HashMap<String, Command> getKnownCommands(CommandMap commandMap) {
        HashMap<String, Command> knownCommands = new HashMap<>();
        try {
            Field knownCommandsField = ((SimpleCommandMap) commandMap).getClass().getDeclaredField("knownCommands");
            knownCommandsField.setAccessible(true);
            knownCommands = (HashMap<String, Command>) knownCommandsField.get(commandMap);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return knownCommands;
    }

    /**
     * Removes a known command from the command map.
     *
     * @param commandName The command name to unregister.
     */
    public void removeKnownCommand(String commandName) {
        HashMap<String, Command> knownCommands = getKnownCommands(getCommandMap());
        knownCommands.remove(commandName);
    }

    /**
     * Gets a current command by the name you specify.
     * @param name The name you are looking for.
     * @return The command by that name or null if it cannot find the command.
     */
    @SuppressWarnings("unused")
    public CoreCommand getCommandByName(String name) {
        return topLevelCommands.get(name);
    }
}
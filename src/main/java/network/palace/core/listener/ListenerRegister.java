package network.palace.core.listener;

import network.palace.core.Core;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;

import java.util.Set;

public class ListenerRegister {

    public ListenerRegister(JavaPlugin plugin) {
        // Create a reflections mapping for the plugin
        Reflections reflections = new Reflections(plugin.getClass().getPackage().getName());
        // Find all commands for the plugin
        Set<Class<? extends Listener>> listeners = reflections.getSubTypesOf(Listener.class);
        for (Class<? extends Listener> listener : listeners) {
            // Allow to specify when to register a listener
            if (listener.isAnnotationPresent(ManualListener.class)) continue;
            // Try to register the listener
            try {
                Core.registerListener(listener.newInstance(), plugin);
                Core.logMessage(plugin.getName(), "Registered Listener > " + listener.getSimpleName());
            } catch (InstantiationException | IllegalAccessException e) {
                Core.logMessage(plugin.getName(), ChatColor.DARK_RED + "Can not create listener " + listener.getSimpleName());
                e.printStackTrace();
            }
        }
    }
}

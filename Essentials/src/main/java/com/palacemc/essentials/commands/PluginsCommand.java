package com.palacemc.essentials.commands;

import com.palacemc.core.Core;
import com.palacemc.core.command.CommandException;
import com.palacemc.core.command.CommandMeta;
import com.palacemc.core.command.CoreCommand;
import com.palacemc.essentials.BoilerplateUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@CommandMeta(aliases = {"pl", "ver", "version"}, description = "Lists the plugins for the server.")
public class PluginsCommand extends CoreCommand {

    public PluginsCommand() {
        super("plugins");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        // Lists
        List<PluginInfo> modulesList = new ArrayList<>();
        List<PluginInfo> pluginsList = new ArrayList<>();
        // String builds
        StringBuilder modulesSB = new StringBuilder();
        StringBuilder pluginSB = new StringBuilder();
        // Loop through plugins and add
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            if (plugin instanceof com.palacemc.core.plugin.Plugin) {
                com.palacemc.core.plugin.Plugin corePlugin = (com.palacemc.core.plugin.Plugin) plugin;
                modulesList.add(new PluginInfo(corePlugin.getInfo().name(), corePlugin.isEnabled()));
            } else if (!(plugin instanceof Core)) {
                pluginsList.add(new PluginInfo(plugin.getName(), plugin.isEnabled()));
            }
        }
        // Sort
        Collections.sort(modulesList, getComparator());
        Collections.sort(pluginsList, getComparator());
        // Set plugin info color
        for (PluginInfo info : modulesList) {
            modulesSB.append(info.isEnabled() ? ChatColor.GREEN : ChatColor.RED).append(info.getName()).append(ChatColor.GOLD).append(", ");
        }
        for (PluginInfo info : pluginsList) {
            pluginSB.append(info.isEnabled() ? ChatColor.GREEN : ChatColor.RED).append(info.getName()).append(ChatColor.GOLD).append(", ");
        }
        String modules = modulesSB.toString();
        modules = modules.substring(0, Math.max(0, modules.length() - 2));
        String plugins = pluginSB.toString();
        plugins = plugins.substring(0, Math.max(0, plugins.length() - 2));
        // Send Messages
        sender.sendMessage(BoilerplateUtil.getBoilerplateText());
        sender.sendMessage(ChatColor.GOLD + "Running " + ChatColor.GREEN + "Core " + ChatColor.GOLD + "version " + ChatColor.DARK_GREEN + Core.getVersion());
        sender.sendMessage("");
        sender.sendMessage(ChatColor.GOLD + "Core Modules: " + ChatColor.RESET + modules);
        sender.sendMessage("");
        sender.sendMessage(ChatColor.RED + "Third Party Plugins: " + ChatColor.RESET + plugins);
        sender.sendMessage(BoilerplateUtil.getBoilerplateText());
    }

    public Comparator getComparator() {
        return (Comparator<PluginInfo>) (info1, info2) -> info1.getName().compareTo(info2.getName());
    }

    public class PluginInfo {
        private String name;
        private boolean enabled;

        public PluginInfo(String name, boolean enabled) {
            this.name = name;
            this.enabled = enabled;
        }

        public String getName() {
            return name;
        }

        public boolean isEnabled() {
            return enabled;
        }
    }
}

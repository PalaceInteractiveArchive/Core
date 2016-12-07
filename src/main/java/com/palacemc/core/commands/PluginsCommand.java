package com.palacemc.core.commands;

import com.palacemc.core.Core;
import com.palacemc.core.command.CommandException;
import com.palacemc.core.command.CommandMeta;
import com.palacemc.core.command.CoreCommand;
import com.palacemc.core.config.LanguageFormatter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@CommandMeta(aliases = {"pl", "ver", "version"}, description = "Lists the plugins for the server.")
public class PluginsCommand extends CoreCommand {

    public PluginsCommand() {
        super("plugins");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        // Formatter
        LanguageFormatter formatter = Core.getLanguageFormatter();
        // Lists
        List<PluginInfo> pluginsList = new ArrayList<>();
        List<PluginInfo> thirdPartyList = new ArrayList<>();
        // String builds
        StringBuilder pluginsSB = new StringBuilder();
        StringBuilder thirdPartySB = new StringBuilder();
        // Loop through plugins and add
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            if (plugin instanceof com.palacemc.core.plugin.Plugin) {
                com.palacemc.core.plugin.Plugin corePlugin = (com.palacemc.core.plugin.Plugin) plugin;
                pluginsList.add(new PluginInfo(corePlugin.getInfo().name(), corePlugin.isEnabled()));
            } else if (!(plugin instanceof Core)) {
                thirdPartyList.add(new PluginInfo(plugin.getName(), plugin.isEnabled()));
            }
        }
        // Sort
        pluginsList.sort(Comparator.comparing(PluginInfo::getName));
        thirdPartyList.sort(Comparator.comparing(PluginInfo::getName));
        // Plugins info and colors
        String pluginsSeparator = formatter.getFormat(sender, "command.plugins.plugins.separator");
        String pluginsEnabledColor = formatter.getFormat(sender, "command.plugins.plugins.enabledColor");
        String pluginsDisabledColor = formatter.getFormat(sender, "command.plugins.plugins.disabledColor");
        for (PluginInfo info : pluginsList) {
            pluginsSB.append(info.isEnabled() ? pluginsEnabledColor : pluginsDisabledColor).append(info.getName()).append(pluginsSeparator);
        }
        // Third party plugins info and colors
        String thirdPartySeparator = formatter.getFormat(sender, "command.plugins.thirdParty.separator");
        String thirdPartyEnabledColor = formatter.getFormat(sender, "command.plugins.thirdParty.enabledColor");
        String thirdPartyDisabledColor = formatter.getFormat(sender, "command.plugins.thirdParty.disabledColor");
        for (PluginInfo info : thirdPartyList) {
            thirdPartySB.append(info.isEnabled() ? thirdPartyEnabledColor : thirdPartyDisabledColor).append(info.getName()).append(thirdPartySeparator);
        }
        // Remove left over separators
        String plugins = pluginsSB.toString();
        plugins = plugins.substring(0, Math.max(0, plugins.length() - pluginsSeparator.length()));
        String thirdParty = thirdPartySB.toString();
        thirdParty = thirdParty.substring(0, Math.max(0, thirdParty.length() - thirdPartySeparator.length()));
        // Formats
        String boilerPlateFormat = formatter.getFormat(sender, "command.plugins.boilerPlate");
        String runningFormat = formatter.getFormat(sender, "command.plugins.running").replaceAll("<core-version>", Double.toString(Core.getVersion()));
        String pluginsFormat = formatter.getFormat(sender, "command.plugins.plugins.info").replaceAll("<core-plugins>", plugins);
        String thirdPartyFormat = formatter.getFormat(sender, "command.plugins.thirdParty.info").replaceAll("<thirdParty-plugins>", thirdParty);
        // Send Messages
        String boilerPlate = BoilerplateUtil.getBoilerplateText(boilerPlateFormat);
        sender.sendMessage(boilerPlate);
        sender.sendMessage(runningFormat);
        sender.sendMessage("");
        sender.sendMessage(pluginsFormat);
        sender.sendMessage("");
        sender.sendMessage(thirdPartyFormat);
        sender.sendMessage(boilerPlate);
    }

    public class PluginInfo {
        private final String name;
        private final boolean enabled;

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

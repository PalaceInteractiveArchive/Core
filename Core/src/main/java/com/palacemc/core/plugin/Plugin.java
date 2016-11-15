package com.palacemc.core.plugin;

import com.palacemc.core.Core;
import com.palacemc.core.command.CoreCommand;
import com.palacemc.core.command.CoreCommandMap;
import com.palacemc.core.config.LanguageFormatter;
import com.palacemc.core.library.LibraryHandler;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class Plugin extends JavaPlugin {

    @Getter private PluginInfo info;
    @Getter private LanguageFormatter languageFormatter;
    @Getter private CoreCommandMap commandMap;

    @Override
    public final void onEnable() {
        try {
            // Start library downloading and loading
            LibraryHandler.loadLibraries(this);
            // Check if Core is enabled if not can't work
            if (!Core.getInstance().isEnabled()) return;
            // Register this plugin to Core
            Core.onPluginEnable(this);
            // Get plugin info
            info = getClass().getAnnotation(PluginInfo.class);
            if (info == null) throw new IllegalStateException("You must annotate your class with the @PluginInfo annotation!");
            // Load languages
            languageFormatter = new LanguageFormatter(this);
            // Start command map
            commandMap = new CoreCommandMap(this);
            // Plugin enabled finally
            onPluginEnable();
        } catch (Exception e) {
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        Core.logMessage(getInfo().name(), ChatColor.DARK_GREEN  + "Plugin Enabled");
    }

    @Override
    public final void onDisable() {
        try {
            onPluginDisable();
            Core.onPluginDisable(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Core.logMessage(getInfo().name(), ChatColor.DARK_RED + "Plugin Disabled");
    }

    /* Delegated Methods */
    protected void onPluginEnable() throws Exception { Core.logMessage(getInfo().name(), ChatColor.RED + "Did not run any code on enable!"); }
    protected void onPluginDisable() throws Exception {}

    /* Command Methods */
    public final <T extends CoreCommand> T registerCommand(T command) {
        getCommandMap().registerCommand(command);
        return command;
    }

    /* Bukkit Utils */
    public void registerListener(Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }
}

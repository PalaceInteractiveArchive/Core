package com.palacemc.core.plugin;

import com.palacemc.core.Core;
import com.palacemc.core.command.CoreCommand;
import com.palacemc.core.command.CoreCommandMap;
import com.palacemc.core.library.LibraryHandler;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class Plugin extends JavaPlugin {

    private PluginInfo info;
    private CoreCommandMap commandMap;

    @Override
    public final void onEnable() {
        try {
            LibraryHandler.loadLibraries(this);
            if (!Core.getInstance().isEnabled()) return;
            Core.onPluginEnable(this);
            info = getClass().getAnnotation(PluginInfo.class);
            if (info == null) throw new IllegalStateException("You must annotate your class with the @PluginInfo annotation!");
            commandMap = new CoreCommandMap(this);
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

    /* Get Methods */
    public PluginInfo getInfo() {
        return info;
    }

    public CoreCommandMap getCommandMap() {
        return commandMap;
    }
}

package com.palacemc.essentials;

import com.palacemc.core.plugin.Plugin;
import com.palacemc.core.plugin.PluginInfo;
import com.palacemc.essentials.commands.ListCommand;
import com.palacemc.essentials.commands.PluginsCommand;

@PluginInfo(name = "Essentials")
public class EssentialsMain extends Plugin {

    @Override
    protected void onPluginEnable() throws Exception  {
        registerCommand(new PluginsCommand());
        registerCommand(new ListCommand());
    }
}

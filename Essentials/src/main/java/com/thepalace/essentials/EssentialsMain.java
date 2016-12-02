package com.thepalace.essentials;

import com.thepalace.core.plugin.Plugin;
import com.thepalace.core.plugin.PluginInfo;
import com.thepalace.essentials.commands.ListCommand;
import com.thepalace.essentials.commands.PluginsCommand;

@PluginInfo(name = "Essentials")
public class EssentialsMain extends Plugin {

    @Override
    protected void onPluginEnable() throws Exception  {
        registerCommand(new PluginsCommand());
        registerCommand(new ListCommand());
    }
}

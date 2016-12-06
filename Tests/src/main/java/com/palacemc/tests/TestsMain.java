package com.palacemc.tests;

import com.thepalace.core.plugin.Plugin;
import com.thepalace.core.plugin.PluginInfo;

@PluginInfo(name = "Tests")
public class TestsMain extends Plugin {

    @Override
    protected void onPluginEnable() throws Exception {
        registerListener(new TestsListener());
    }
}

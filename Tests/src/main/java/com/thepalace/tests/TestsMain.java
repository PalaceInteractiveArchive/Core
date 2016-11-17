package com.thepalace.tests;

import com.thepalace.core.plugin.Plugin;
import com.thepalace.core.plugin.PluginInfo;

@PluginInfo(name = "Tests")
public class TestsMain extends Plugin {

    @Override
    protected void onPluginEnable() {
        registerListener(new TestsListener());
    }

    @Override
    protected void onPluginDisable() {

    }
}

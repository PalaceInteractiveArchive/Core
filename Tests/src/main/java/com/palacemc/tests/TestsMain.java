package com.palacemc.tests;

import com.palacemc.core.plugin.Plugin;
import com.palacemc.core.plugin.PluginInfo;

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

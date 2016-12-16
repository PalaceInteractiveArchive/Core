package com.palacemc.core.commands;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.palacemc.core.Core;
import com.palacemc.core.command.CommandException;
import com.palacemc.core.command.CommandMeta;
import com.palacemc.core.command.CommandPermission;
import com.palacemc.core.command.CoreCommand;
import com.palacemc.core.config.LanguageFormatter;
import com.palacemc.core.player.Rank;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

@CommandMeta(aliases = {"about", "pl", "ver", "version"}, description = "Lists the plugins for the server.")
@CommandPermission(rank = Rank.WIZARD)
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
        String runningFormat = formatter.getFormat(sender, "command.plugins.running").replaceAll("<core-version>", Core.getVersion());
        String pluginsFormat = formatter.getFormat(sender, "command.plugins.plugins.info").replaceAll("<core-plugins>", plugins).replaceAll("<count>", Integer.toString(pluginsList.size()));
        String thirdPartyFormat = formatter.getFormat(sender, "command.plugins.thirdParty.info").replaceAll("<thirdParty-plugins>", thirdParty).replaceAll("<count>", Integer.toString(thirdPartyList.size()));
        // Send Messages
        String boilerPlate = BoilerplateUtil.getBoilerplateText(boilerPlateFormat);
        sender.sendMessage(boilerPlate);
        sender.sendMessage(runningFormat);
        sender.sendMessage("");
        sender.sendMessage(pluginsFormat);
        sender.sendMessage("");
        sender.sendMessage(thirdPartyFormat);
        sender.sendMessage(boilerPlate);
        sendVersion(sender);
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

    private final ReentrantLock versionLock = new ReentrantLock();
    private boolean hasVersion = false;
    private String versionMessage = null;
    private final Set<CommandSender> versionWaiters = new HashSet<>();
    private boolean versionTaskStarted = false;
    private long lastCheck = 0;

    private void sendVersion(CommandSender sender) {
        if (hasVersion) {
            if (System.currentTimeMillis() - lastCheck > 21600000) {
                lastCheck = System.currentTimeMillis();
                hasVersion = false;
            } else {
                sender.sendMessage(versionMessage);
                return;
            }
        }
        versionLock.lock();
        try {
            if (hasVersion) {
                sender.sendMessage(versionMessage);
                return;
            }
            versionWaiters.add(sender);
            sender.sendMessage("Checking version, please wait...");
            if (!versionTaskStarted) {
                versionTaskStarted = true;
                new Thread(this::obtainVersion).start();
            }
        } finally {
            versionLock.unlock();
        }
    }

    private void obtainVersion() {
        String version = Bukkit.getVersion();
        if (version == null) version = "Custom";
        if (version.startsWith("git-Spigot-")) {
            String[] parts = version.substring("git-Spigot-".length()).split("-");
            int cbVersions = getDistance("craftbukkit", parts[1].substring(0, parts[1].indexOf(' ')));
            int spigotVersions = getDistance("spigot", parts[0]);
            if (cbVersions == -1 || spigotVersions == -1) {
                setVersionMessage("Error obtaining version information");
            } else {
                if (cbVersions == 0 && spigotVersions == 0) {
                    setVersionMessage("You are running the latest version");
                } else {
                    setVersionMessage("You are " + (cbVersions + spigotVersions) + " version(s) behind");
                }
            }

        } else if (version.startsWith("git-Bukkit-")) {
            version = version.substring("git-Bukkit-".length());
            int cbVersions = getDistance("craftbukkit", version.substring(0, version.indexOf(' ')));
            if (cbVersions == -1) {
                setVersionMessage("Error obtaining version information");
            } else {
                if (cbVersions == 0) {
                    setVersionMessage("You are running the latest version");
                } else {
                    setVersionMessage("You are " + cbVersions + " version(s) behind");
                }
            }
        } else {
            setVersionMessage("Unknown version, custom build?");
        }
    }

    private void setVersionMessage(String msg) {
        lastCheck = System.currentTimeMillis();
        versionMessage = msg;
        versionLock.lock();
        try {
            hasVersion = true;
            versionTaskStarted = false;
            for (CommandSender sender : versionWaiters) {
                sender.sendMessage(versionMessage);
            }
            versionWaiters.clear();
        } finally {
            versionLock.unlock();
        }
    }

    private static int getDistance(String repo, String hash) {
        try {
            try (BufferedReader reader = Resources.asCharSource(new URL("https://hub.spigotmc.org/stash/rest/api/1.0/projects/SPIGOT/repos/" + repo + "/commits?since=" + URLEncoder.encode(hash, "UTF-8") + "&withCounts=true"),
                    Charsets.UTF_8
            ).openBufferedStream()) {
                JSONObject obj = (JSONObject) new JSONParser().parse(reader);
                return ((Number) obj.get("totalCount")).intValue();
            } catch (ParseException ex) {
                ex.printStackTrace();
                return -1;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }
}

package network.palace.core.commands;

import com.comphenix.protocol.utility.MinecraftVersion;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import lombok.Getter;
import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.message.FormattedMessage;
import network.palace.core.player.Rank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.annotation.IncompleteAnnotationException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * The type Plugins command.
 */
@CommandMeta(aliases = {"about", "pl", "ver", "version", "help", "?"}, description = "Lists the plugins for the server.", rank = Rank.DEVELOPER)
public class PluginsCommand extends CoreCommand {

    private String versionsBehind = "Loading version...";

    /**
     * Instantiates a new Plugins command.
     */
    public PluginsCommand() {
        super("plugins");
        Core.runTaskAsynchronously(Core.getInstance(), this::obtainVersion);
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        // Check if sender is a player
        boolean isPlayer = sender instanceof Player;
        // Lists
        List<PluginInfo> pluginsList = new ArrayList<>();
        List<PluginInfo> thirdPartyList = new ArrayList<>();
        // Loop through plugins and add
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            if (plugin instanceof network.palace.core.plugin.Plugin) {
                network.palace.core.plugin.Plugin corePlugin = (network.palace.core.plugin.Plugin) plugin;
                String version;
                try {
                    version = corePlugin.getInfo().version();
                } catch (IncompleteAnnotationException ignored) {
                    version = corePlugin.getDescription().getVersion();
                }
                pluginsList.add(new PluginInfo(corePlugin.getInfo().name(), version, corePlugin.isEnabled()));
            } else if (!(plugin instanceof Core)) {
                thirdPartyList.add(new PluginInfo(plugin.getName(), plugin.getDescription().getVersion(), plugin.isEnabled()));
            }
        }
        // Sort
        pluginsList.sort(Comparator.comparing(PluginInfo::getName));
        thirdPartyList.sort(Comparator.comparing(PluginInfo::getName));
        // Plugins info and colors
        FormattedMessage pluginsFM = createPluginListMessage(isPlayer, pluginsList);
        // Third party plugins info and colors
        FormattedMessage thirdPartyFM = createPluginListMessage(isPlayer, thirdPartyList);
        // Boilerplate text
        String boilerPlate = BoilerplateUtil.getBoilerplateText(ChatColor.GOLD.toString() + ChatColor.STRIKETHROUGH.toString());
        // Send messages
        sender.sendMessage(boilerPlate);
        sender.sendMessage(ChatColor.GOLD + "Running " + ChatColor.GREEN + "Core " + ChatColor.GOLD + "version " + ChatColor.DARK_GREEN + Core.getVersion());
        sender.sendMessage("");
        if (isPlayer) {
            MinecraftVersion current = new MinecraftVersion(Bukkit.getServer());
            String currentVersion = ChatColor.DARK_GREEN + current.getVersion();
            String spigotVersion = ChatColor.GOLD + "Spigot version: " + ChatColor.GREEN + Bukkit.getVersion();
            String apiVersion = ChatColor.GOLD + "API Version: " + ChatColor.GREEN + Bukkit.getBukkitVersion();
            String versionInfo = ChatColor.GOLD + "Version Info: " + ChatColor.GREEN + versionsBehind;
            FormattedMessage spigot = new FormattedMessage("Running ").color(ChatColor.GOLD).then("Spigot ").color(ChatColor.GREEN).then("version ").color(ChatColor.GOLD).then(currentVersion).tooltip(currentVersion, spigotVersion, apiVersion, versionInfo);
            spigot.send((Player) sender);
        } else {
            sender.sendMessage(ChatColor.GOLD + "Running " + ChatColor.GREEN + "Spigot " + ChatColor.GOLD + "version " + ChatColor.DARK_GREEN + Bukkit.getVersion() + " (API version " + Bukkit.getBukkitVersion() + ") " + ChatColor.YELLOW + "(" + versionsBehind + ")");
        }
        sender.sendMessage("");
        if (isPlayer) {
            pluginsFM.send((Player) sender);
        } else {
            sender.sendMessage(pluginsFM.toFriendlyString());
        }
        sender.sendMessage("");
        if (isPlayer) {
            thirdPartyFM.send((Player) sender);
        } else {
            sender.sendMessage(thirdPartyFM.toFriendlyString());
        }
        sender.sendMessage(boilerPlate);
    }

    /**
     * The type Plugin info.
     */
    public static class PluginInfo {

        @Getter private final String name;
        @Getter private final String version;
        @Getter private final boolean enabled;

        public PluginInfo(String name, String version, boolean enabled) {
            this.name = name;
            this.version = ((version == null || version.trim().isEmpty()) ? "Unknown" : version);
            this.enabled = enabled;
        }

    }

    private void obtainVersion() {
        String version = Bukkit.getVersion();
        if (version == null) version = "Custom";
        String[] parts = version.substring(0, version.indexOf(' ')).split("-");
        if (parts.length == 4) {
            int cbVersions = getDistance("craftbukkit", parts[3]);
            int spigotVersions = getDistance("spigot", parts[2]);
            if (cbVersions == -1 || spigotVersions == -1) {
                versionsBehind = "Error obtaining version information";
            } else {
                if (cbVersions == 0 && spigotVersions == 0) {
                    versionsBehind = "You are running the latest version";
                } else {
                    versionsBehind = "You are " + (cbVersions + spigotVersions) + " version(s) behind";
                }
            }

        } else if (parts.length == 3) {
            int cbVersions = getDistance("craftbukkit", parts[2]);
            if (cbVersions == -1) {
                versionsBehind = "Error obtaining version information";
            } else {
                if (cbVersions == 0) {
                    versionsBehind = "You are running the latest version";
                } else {
                    versionsBehind = "You are " + cbVersions + " version(s) behind";
                }
            }
        } else {
            versionsBehind = "Unknown version";
        }
    }


    private static int getDistance(String repo, String hash) {
        try {
            try (BufferedReader reader = Resources.asCharSource(
                    new URL("https://hub.spigotmc.org/stash/rest/api/1.0/projects/SPIGOT/repos/" + repo + "/commits?since=" + URLEncoder.encode(hash, StandardCharsets.UTF_8) + "&withCounts=true"),
                    Charsets.UTF_8
            ).openBufferedStream()) {
                JsonObject obj = new Gson().fromJson(reader, JsonObject.class);
                return obj.get("totalCount").getAsInt();
            } catch (JsonSyntaxException ex) {
                ex.printStackTrace();
                return -1;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private FormattedMessage createPluginListMessage(boolean isPlayer, List<PluginInfo> thirdPartyList) {
        FormattedMessage msg = new FormattedMessage("");
        for (int i = 0; i < thirdPartyList.size(); i++) {
            PluginInfo info = thirdPartyList.get(i);
            msg.then(info.getName()).color(info.isEnabled() ? ChatColor.GREEN : ChatColor.RED);
            if (isPlayer) {
                msg.tooltip(ChatColor.GOLD + "Version: " + ChatColor.GREEN + info.getVersion());
            } else {
                msg.then(" (" + info.getVersion() + ")").color(ChatColor.YELLOW);
            }
            if (i != thirdPartyList.size() - 1) {
                msg.then(", ").color(ChatColor.GOLD);
            }
        }
        return msg;
    }
}

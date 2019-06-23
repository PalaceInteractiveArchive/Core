package network.palace.core.commands;

import com.comphenix.protocol.utility.MinecraftVersion;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
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
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.annotation.IncompleteAnnotationException;
import java.net.URL;
import java.net.URLEncoder;
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
        // Lists
        List<PluginInfo> pluginsList = new ArrayList<>();
        List<PluginInfo> thirdPartyList = new ArrayList<>();
        // String builds
        FormattedMessage pluginsFM = new FormattedMessage("");
        FormattedMessage thirdPartyFM = new FormattedMessage("");
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
        for (int i = 0; i < pluginsList.size(); i++) {
            PluginInfo info = pluginsList.get(i);
            pluginsFM.then(info.getName()).color(info.isEnabled() ? ChatColor.GREEN : ChatColor.RED).tooltip(ChatColor.GOLD + "Version: " + ChatColor.GREEN + info.getVersion());
            if (i != pluginsList.size() - 1) {
                pluginsFM.then(", ").color(ChatColor.GOLD);
            }
        }
        // Third party plugins info and colors
        for (int i = 0; i < thirdPartyList.size(); i++) {
            PluginInfo info = thirdPartyList.get(i);
            thirdPartyFM.then(info.getName()).color(info.isEnabled() ? ChatColor.GREEN : ChatColor.RED).tooltip(ChatColor.GOLD + "Version: " + ChatColor.GREEN + info.getVersion());
            if (i != thirdPartyList.size() - 1) {
                thirdPartyFM.then(", ").color(ChatColor.GOLD);
            }
        }
        // Check if sender is a player
        boolean isPlayer = sender instanceof Player;
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
    public class PluginInfo {
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
        if (version.startsWith("git-Spigot-")) {
            String[] parts = version.substring("git-Spigot-".length()).split("-");
            int cbVersions = getDistance("craftbukkit", parts[1].substring(0, parts[1].indexOf(' ')));
            int spigotVersions = getDistance("spigot", parts[0]);
            if (cbVersions == -1 || spigotVersions == -1) {
                versionsBehind = "Error obtaining version information";
            } else {
                if (cbVersions == 0 && spigotVersions == 0) {
                    versionsBehind = "Latest";
                } else {
                    versionsBehind = (cbVersions + spigotVersions) + " behind";
                }
            }
        } else if (version.startsWith("git-Bukkit-")) {
            version = version.substring("git-Bukkit-".length());
            int cbVersions = getDistance("craftbukkit", version.substring(0, version.indexOf(' ')));
            if (cbVersions == -1) {
                versionsBehind = "Error obtaining version information";
            } else {
                if (cbVersions == 0) {
                    versionsBehind = "Latest";
                } else {
                    versionsBehind = cbVersions + " behind";
                }
            }
        } else {
            versionsBehind = "Unknown";
        }
    }

    private static int getDistance(String repo, String hash) {
        try {
            try (BufferedReader reader = Resources.asCharSource(new URL("https://hub.spigotmc.org/stash/rest/api/1.0/projects/SPIGOT/repos/"
                    + repo + "/commits?since=" + URLEncoder.encode(hash, "UTF-8") + "&withCounts=true"), Charsets.UTF_8
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

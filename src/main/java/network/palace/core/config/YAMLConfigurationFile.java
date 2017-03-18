package network.palace.core.config;

import lombok.NonNull;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

/**
 * The type Yaml configuration file.
 */
public final class YAMLConfigurationFile {

    private final String path;
    private final File file;
    private final JavaPlugin plugin;

    private File configFile;
    private FileConfiguration fileConfiguration;

    /**
     * Instantiates a new Yaml configuration file.
     *
     * @param plugin   the plugin
     * @param fileName the file name
     */
    public YAMLConfigurationFile(@NonNull JavaPlugin plugin, @NonNull String fileName) {
        this(plugin, "", fileName);
    }

    /**
     * Instantiates a new Yaml configuration file.
     *
     * @param plugin   the plugin
     * @param path     the path
     * @param fileName the file name
     */
    public YAMLConfigurationFile(@NonNull JavaPlugin plugin, String path, @NonNull String fileName) {
        this(plugin, path, new File(plugin.getDataFolder(), fileName));
    }

    /**
     * Instantiates a new Yaml configuration file.
     *
     * @param plugin the plugin
     * @param path   the path
     * @param file   the file
     */
    public YAMLConfigurationFile(@NonNull JavaPlugin plugin, String path, @NonNull File file) {
        if (!plugin.isEnabled()) {
            throw new IllegalArgumentException("Plugin must be enabled");
        }
        this.path = path;
        this.plugin = plugin;
        this.file = file;
        File dataFolder = plugin.getDataFolder();
        if (dataFolder == null) {
            throw new IllegalStateException();
        }
        this.configFile = file;
        saveDefaultConfig();
    }

    /**
     * Reload config.
     */
    public void reloadConfig() {
        fileConfiguration = YamlConfiguration.loadConfiguration(configFile);
        // Look for defaults in the jar
        if (file != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(file);
            fileConfiguration.setDefaults(defConfig);
        }
    }

    /**
     * Gets config.
     *
     * @return the config
     */
    public FileConfiguration getConfig() {
        if (fileConfiguration == null) reloadConfig();
        return fileConfiguration;
    }

    /**
     * Gets defaults.
     *
     * @return the defaults
     */
    public Configuration getDefaults() {
        if (fileConfiguration == null) reloadConfig();
        return fileConfiguration.getDefaults();
    }

    /**
     * Save config.
     */
    public void saveConfig() {
        if (fileConfiguration == null || configFile == null) {
            return;
        }
        try {
            getConfig().save(configFile);
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE, "Could not save config to " + configFile, ex);
        }
    }

    /**
     * Save default config.
     */
    public void saveDefaultConfig() {
        if (!configFile.exists()) {
            plugin.saveResource(path + file.getName(), false);
        }
    }
}

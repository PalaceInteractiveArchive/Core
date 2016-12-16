package com.palacemc.core.config;

import lombok.NonNull;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public final class YAMLConfigurationFile {

    private final String path;
    private final File file;
    private final JavaPlugin plugin;

    private File configFile;
    private FileConfiguration fileConfiguration;

    public YAMLConfigurationFile(@NonNull JavaPlugin plugin, String path, @NonNull String fileName) {
        this(plugin, path, new File(plugin.getDataFolder(), fileName));
    }

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

    public void reloadConfig() {
        fileConfiguration = YamlConfiguration.loadConfiguration(configFile);
        // Look for defaults in the jar
        if (file != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(file);
            fileConfiguration.setDefaults(defConfig);
        }
    }

    public FileConfiguration getConfig() {
        if (fileConfiguration == null) reloadConfig();
        return fileConfiguration;
    }

    @SuppressWarnings("unused")
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

    public void saveDefaultConfig() {
        if (!configFile.exists()) {
            plugin.saveResource(path + file.getName(), false);
        }
    }
}

package com.palacemc.core.config;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

public class LanguageFormatter {

    private static final String DEFAULT_LANG = "en_US";
    private static final String PATH = "lang/";
    private final String LANG_FILE_NAME = "languages.txt";
    private final String YML_EXTENSION = ".yml";

    private HashMap<String, YAMLConfigurationFile> languages = new HashMap<>();

    public LanguageFormatter(JavaPlugin plugin) throws IOException {
        // Return if languages file does not exist
        if (plugin.getResource(PATH + LANG_FILE_NAME) == null) return;
        // Languages text file to get all the languages to load
        BufferedReader languagesReader = new BufferedReader(new InputStreamReader(plugin.getResource(PATH + LANG_FILE_NAME)));
        String language;
        while ((language = languagesReader.readLine()) != null) {
            // Check if resource exist if not continue
            if (plugin.getResource(PATH + language + YML_EXTENSION) == null) continue;
            // Add to list of languages
            languages.put(language, new YAMLConfigurationFile(plugin, PATH, PATH + language + YML_EXTENSION));
        }
    }

    public final String getFormat(String lang, String key) {
        String format = getFormatFromLang(lang, key);
        if (format != null) {
            return ChatColor.translateAlternateColorCodes('&', format);
        } else {
            format = getFormatFromLang(DEFAULT_LANG, key);
            if (format != null) {
                return ChatColor.translateAlternateColorCodes('&', format);
            } else {
                return null;
            }
        }
    }

    public String getFormatFromLang(String lang, String key) {
        if (getYamlFile(lang) == null) return null;
        FileConfiguration langFile = getYamlFile(lang).getConfig();
        if (langFile == null) return null;
        if (langFile.contains(key)) {
            return langFile.getString(key);
        } else {
            return null;
        }
    }

    public YAMLConfigurationFile getYamlFile(String lang) {
        return languages.get(lang);
    }
}

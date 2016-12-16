package network.palace.core.config;

import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
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

    private final HashMap<String, YAMLConfigurationFile> languages = new HashMap<>();

    public LanguageFormatter(JavaPlugin plugin) {
        if (plugin == null) return;
        // Return if languages file does not exist
        if (plugin.getResource(PATH + LANG_FILE_NAME) == null) return;
        // Languages text file to get all the languages to load
        try {
            BufferedReader languagesReader = new BufferedReader(new InputStreamReader(plugin.getResource(PATH + LANG_FILE_NAME)));
            String language;
            while ((language = languagesReader.readLine()) != null) {
                // Check if resource exist if not continue
                if (plugin.getResource(PATH + language + YML_EXTENSION) == null) continue;
                // Add to list of languages
                languages.put(language, new YAMLConfigurationFile(plugin, PATH, PATH + language + YML_EXTENSION));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public final String getFormat(CommandSender sender, String key) {
        String locale = DEFAULT_LANG;
        if (sender instanceof Player) {
            CPlayer player = Core.getPlayerManager().getPlayer((Player) sender);
            locale = player.getLocale();
        }
        return getFormat(locale, key);
    }

    public final String getFormat(String locale, String key) {
        String format = getFormatFromLang(locale, key);
        if (format != null) {
            return ChatColor.translateAlternateColorCodes('&', format);
        } else {
            format = getFormatFromLang(DEFAULT_LANG, key);
            if (format != null) {
                return ChatColor.translateAlternateColorCodes('&', format);
            } else {
                return "";
            }
        }
    }

    private String getFormatFromLang(String lang, String key) {
        if (getYamlFile(lang) == null) return null;
        FileConfiguration langFile = getYamlFile(lang).getConfig();
        if (langFile == null) return null;
        if (langFile.contains(key)) {
            return langFile.getString(key);
        } else {
            return null;
        }
    }

    private YAMLConfigurationFile getYamlFile(String lang) {
        return languages.get(lang);
    }
}

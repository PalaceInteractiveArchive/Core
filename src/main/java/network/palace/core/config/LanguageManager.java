package network.palace.core.config;

import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.core.utils.MiscUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.HashMap;

/**
 * The type Language manager.
 */
public class LanguageManager {
    private final String url = "https://spreadsheets.google.com/feeds/cells/1Sy0BswfWXjGybjchn6YGj68fN19cl4P4wtrEF8EkPhw/od6/public/basic?alt=json";
    public static final String DEFAULT_LANG = "en_US";

    private final HashMap<String, HashMap<String, String>> languages = new HashMap<>();

    /**
     * Instantiates a new Language manager.
     */
    public LanguageManager() {
        Core.runTaskTimerAsynchronously(this::reload, 0L, 6000L);
    }

    public void reload() {
        JSONObject obj = MiscUtil.readJsonFromUrl(url);
        if (obj == null) return;

        JSONArray array = (JSONArray) ((JSONObject) obj.get("feed")).get("entry");
        languages.clear();
        HashMap<Integer, String> langs = new HashMap<>();
        String key = "";
        for (Object objectArray : array) {
            JSONObject object = (JSONObject) objectArray;
            JSONObject content = (JSONObject) object.get("content");
            JSONObject id = (JSONObject) object.get("title");
            String column = (String) id.get("$t");
            String letter = column.substring(0, 1).toLowerCase();
            int row = Integer.parseInt(column.substring(1));
            String text = (String) content.get("$t");
            if (text == null || text.isEmpty()) continue;
            if (row == 1) {
                if (!text.equals("node")) {
                    languages.put(text, new HashMap<>());
                    langs.put(getColumnInt(letter), text);
                }
                continue;
            }
            if (letter.equalsIgnoreCase("a")) {
                key = text;
                continue;
            }
            HashMap<String, String> map = languages.get(langs.get(getColumnInt(letter)));
            map.put(key, text);
        }
    }

    private int getColumnInt(String s) {
        return s.toLowerCase().charAt(0) - 97;
    }

    /**
     * Gets format.
     *
     * @param sender the sender
     * @param key    the key
     * @return the format
     */
    public final String getFormat(CommandSender sender, String key) {
        String locale = DEFAULT_LANG;
        if (sender instanceof Player) {
            CPlayer player = Core.getPlayerManager().getPlayer((Player) sender);
            locale = player.getLocale();
        }
        return getFormat(locale, key);
    }

    /**
     * Get format from player's selected locale
     *
     * @param player the player
     * @param key    the key
     * @return the format from player's locale
     */
    public String getFormat(CPlayer player, String key) {
        String locale = player.getLocale();
        return getFormat(locale, key);
    }

    /**
     * Gets format.
     *
     * @param locale the locale
     * @param key    the key
     * @return the format
     */
    public String getFormat(String locale, String key) {
        HashMap<String, String> lang = languages.get(locale);
        if (lang == null) {
            lang = languages.get(DEFAULT_LANG);
        }
        return ChatColor.translateAlternateColorCodes('&', lang.getOrDefault(key, ""));
    }
}

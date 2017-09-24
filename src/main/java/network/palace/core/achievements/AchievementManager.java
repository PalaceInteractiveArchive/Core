package network.palace.core.achievements;

import com.google.common.collect.ImmutableList;
import network.palace.core.Core;
import network.palace.core.mongo.MongoHandler;
import network.palace.core.utils.MiscUtil;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Created by Marc on 6/26/16
 */
public class AchievementManager {
    public String url = "https://spreadsheets.google.com/feeds/cells/14OHnSeMJVmtFnE7xIdCzMaE0GPOR3Sh4SyE-ZR3hQ7o/od6/public/basic?alt=json";
    private Map<Integer, CoreAchievement> achievements = new HashMap<>();
    private Map<UUID, List<Integer>> earned = new HashMap<>();

    /**
     * Create a new achievement manager
     */
    public AchievementManager() {
        init();
    }

    /**
     * Initialize the achievement manager
     */
    private void init() {
        Core.runTaskTimerAsynchronously(this::reload, 0L, 6000L);

        Core.runTaskTimerAsynchronously(() -> {
            MongoHandler handler = Core.getMongoHandler();
            new HashSet<>(earned.entrySet()).stream().forEach(entry -> entry.getValue().stream().forEach(i -> {
                handler.addAchievement(entry.getKey(), i);
            }));
        }, 0L, 100L);
    }

    /**
     * Reload all achievements
     */
    public void reload() {
        JSONObject obj = readJsonFromUrl(url);
        if (obj == null) return;

        JSONArray array = (JSONArray) ((JSONObject) obj.get("feed")).get("entry");
        achievements.clear();
        CoreAchievement lastAchievement = null;
        for (Object objectArray : array) {
            JSONObject object = (JSONObject) objectArray;
            JSONObject content = (JSONObject) object.get("content");
            JSONObject id = (JSONObject) object.get("title");
            String column = (String) id.get("$t");
            if (!MiscUtil.checkIfInt(column)) continue;
            switch (column.substring(0, 1).toLowerCase()) {
                case "a":
                    lastAchievement = new CoreAchievement(Integer.parseInt((String) content.get("$t")), null, null);
                    break;
                case "b":
                    lastAchievement.setDisplayName((String) content.get("$t"));
                    break;
                case "c":
                    lastAchievement.setDescription((String) content.get("$t"));
                    achievements.put(lastAchievement.getId(), lastAchievement);
                    break;
            }
        }
    }

    /**
     * Get an achievement by ID
     *
     * @param id the id to get from
     * @return the achievement
     */
    public CoreAchievement getAchievement(int id) {
        return achievements.get(id);
    }

    /**
     * Get all achievements.
     *
     * @return all achievements
     */
    public ImmutableList<CoreAchievement> getAchievements() {
        return ImmutableList.copyOf(achievements.values());
    }

    private static JSONObject readJsonFromUrl(String url) {
        try (InputStream is = new URL(url).openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONParser parser = new JSONParser();
            return (JSONObject) parser.parse(jsonText);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }
}
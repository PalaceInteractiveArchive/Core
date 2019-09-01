package network.palace.core.achievements;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import network.palace.core.Core;
import network.palace.core.mongo.MongoHandler;
import network.palace.core.utils.MiscUtil;

import java.util.*;

/**
 * Created by Marc on 6/26/16
 */
public class AchievementManager {
    private static final String url = "https://spreadsheets.google.com/feeds/cells/14OHnSeMJVmtFnE7xIdCzMaE0GPOR3Sh4SyE-ZR3hQ7o/od6/public/basic?alt=json";
    private final Map<Integer, CoreAchievement> achievements = new HashMap<>();
    private final Map<UUID, List<Integer>> earned = new HashMap<>();

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
        Core.runTaskTimerAsynchronously(Core.getInstance(), this::reload, 0L, 6000L);

        Core.runTaskTimerAsynchronously(Core.getInstance(), () -> {
            MongoHandler handler = Core.getMongoHandler();
            new HashSet<>(earned.entrySet()).forEach(entry -> entry.getValue().forEach(i -> {
                handler.addAchievement(entry.getKey(), i);
            }));
        }, 0L, 100L);
    }

    /**
     * Reload all achievements
     */
    public void reload() {
        JsonObject obj = MiscUtil.readJsonFromUrl(url);
        if (obj == null) return;

        JsonArray array = (JsonArray) ((JsonObject) obj.get("feed")).get("entry");
        achievements.clear();
        CoreAchievement lastAchievement = null;
        for (JsonElement elementArray : array) {
            JsonObject object = (JsonObject) elementArray;
            JsonObject content = object.getAsJsonObject("content");
            JsonObject id = object.getAsJsonObject("title");
            String column = id.get("$t").getAsString();
            switch (column.substring(0, 1).toLowerCase()) {
                case "a":
                    lastAchievement = new CoreAchievement(Integer.parseInt(content.get("$t").getAsString()), null, null);
                    break;
                case "b":
                    lastAchievement.setDisplayName(content.get("$t").getAsString());
                    break;
                case "c":
                    lastAchievement.setDescription(content.get("$t").getAsString());
                    achievements.put(lastAchievement.getId(), lastAchievement);
                    break;
            }
        }
        Core.getPlayerManager().getOnlinePlayers().forEach(player -> Core.getCraftingMenu().update(player));
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
}
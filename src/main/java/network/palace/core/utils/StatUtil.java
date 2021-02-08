package network.palace.core.utils;

import com.comphenix.protocol.utility.MinecraftReflection;
import network.palace.core.Core;
import network.palace.core.dashboard.packets.dashboard.PacketLogStatistic;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class StatUtil {

    public StatUtil() throws IOException {
        FileConfiguration config = Core.getCoreConfig();
        if (!config.contains("playground")) {
            config.set("playground", false);
            config.save(new File("plugins/Core/config.yml"));
        }
        int production = Core.isPlayground() ? 0 : 1;
        Core.runTaskTimer(Core.getInstance(), () -> {
            try {
                Class<?> minecraftServer = MinecraftReflection.getMinecraftServerClass();
                Object serverInstance = minecraftServer.getDeclaredMethod("getServer").invoke(minecraftServer);
                double[] recentTps = (double[]) serverInstance.getClass().getField("recentTps").get(serverInstance);

                HashMap<String, Object> tags = new HashMap<>();
                tags.put("server_name", Core.getInstanceName());
                tags.put("production", production);

                HashMap<String, Object> values = new HashMap<>();
                values.put("tps", (float) Math.min(Math.round(recentTps[0] * 100.0) / 100.0, 20.0));
                logStatistic("ticks_per_second", tags, values);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | NoSuchFieldException e) {
                e.printStackTrace();
            }
            try {
                HashMap<String, Object> tags = new HashMap<>();
                tags.put("server_name", Core.getInstanceName());
                tags.put("production", production);

                HashMap<String, Object> values = new HashMap<>();
                values.put("count", Bukkit.getOnlinePlayers().size());

                logStatistic("server_player_count", tags, values);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 40L, 600L);
    }

    public void logStatistic(String tableName, HashMap<String, Object> tags, HashMap<String, Object> values) {
        Core.getDashboardConnection().send(new PacketLogStatistic(tableName, tags, values));
    }
}

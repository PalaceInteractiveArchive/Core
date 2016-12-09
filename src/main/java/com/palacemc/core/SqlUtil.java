package com.palacemc.core;

import com.palacemc.core.player.Rank;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.sql.*;
import java.util.UUID;

/**
 * Created by Marc on 12/8/16.
 */
public class SqlUtil {
    private String url = "";
    private String user = "";
    private String password = "";

    public SqlUtil() {
        loadLogin();
    }

    public void loadLogin() {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new File("plugins/Core/config.yml"));
        url = config.getString("sql.url");
        user = config.getString("sql.user");
        password = config.getString("sql.password");
    }

    public Connection getConnection() {
        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Rank getRank(UUID uuid) {
        try (Connection connection = getConnection()) {
            PreparedStatement sql = connection.prepareStatement("SELECT rank FROM player_data WHERE uuid=?");
            sql.setString(1, uuid.toString());
            ResultSet result = sql.executeQuery();
            if (!result.next()) {
                return Rank.SETTLER;
            }
            Rank rank = Rank.fromString(result.getString("rank"));
            result.close();
            sql.close();
            return rank;
        } catch (SQLException e) {
            e.printStackTrace();
            return Rank.SETTLER;
        }
    }
}

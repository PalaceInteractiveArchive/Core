package network.palace.core.utils;

import network.palace.core.Core;
import network.palace.core.honor.HonorMapping;
import network.palace.core.honor.TopHonorReport;
import network.palace.core.mongo.JoinReport;
import network.palace.core.npc.mob.MobPlayerTexture;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.core.tracking.GameType;
import network.palace.core.tracking.StatisticType;
import org.bukkit.ChatColor;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Utility functions for interacting with MySQL.
 */
public class SqlUtil {
    private String url = "";
    private String user = "";
    private String password = "";

    /**
     * Instantiates a new Sql util.
     */
    public SqlUtil() {
        loadLogin();
    }

    /**
     * Load login.
     */
    private void loadLogin() {
        url = Core.getCoreConfig().getString("sql.url");
        user = Core.getCoreConfig().getString("sql.user");
        password = Core.getCoreConfig().getString("sql.password");
    }

    /**
     * Gets sql connection.
     *
     * @return the connection
     */
    public Connection getConnection() {
        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            if (!Core.isDashboardAndSqlDisabled()) {
                Core.logMessage("Core", ChatColor.RED + "Could not connect to database!");
                ErrorUtil.displayError(e);
            }
            return null;
        }
    }

    /**
     * Get the cached skin for a player's uuid
     *
     * @param uuid The uuid to find
     * @return The texture
     */
    public MobPlayerTexture getPlayerTextureHash(UUID uuid) {
        Connection connection = getConnection();
        MobPlayerTexture texture = new MobPlayerTexture("", "");

        if (connection == null) {
            Core.logInfo("Core > Could not get player hash! - Cannot connect to MySQL!");
            ErrorUtil.displayError("Unable to connect to MySQL!");
            return texture;
        }

        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM skins WHERE uuid=?");
            statement.setString(1, uuid.toString());
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                texture = new MobPlayerTexture(result.getString("value"), result.getString("signature"));
            }
            statement.close();
            result.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            ErrorUtil.displayError(e);
        }
        return texture;
    }

    public void earnCosmetic(CPlayer player, int id) {
        earnCosmetic(player.getUuid(), id);
    }

    /**
     * Earn a cosmetic for a player
     *
     * @param uuid the uuid that earned it
     * @param id   the id of the cosmetic that they earned
     */
    public void earnCosmetic(UUID uuid, int id) {
        Connection connection = getConnection();
        if (connection == null) {
            Core.logInfo("Core > Unable to give player cosmetic item - Cannot connect to MySql!");
            ErrorUtil.displayError("Unable to connect to MySQL!");
            return;
        }

        if (hasCosmetic(uuid, id)) return;
        try {
            // Create a new statement
            PreparedStatement statement = connection.prepareStatement("INSERT INTO cosmetics (uuid,cosmetic,`status`) VALUES (?,?,?)");
            // Set the params and execute
            statement.setString(1, uuid.toString());
            statement.setInt(2, id);
            statement.setBoolean(3, true);
            statement.execute();
            // Finish up
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            ErrorUtil.displayError(e);
        }
    }

    public boolean hasCosmetic(CPlayer player, int id) {
        return hasCosmetic(player.getUuid(), id);
    }

    /**
     * Does a player have a cosmetic item?
     *
     * @param uuid the uuid to check
     * @param id   the id to check
     * @return if the player has the cosmetic
     */
    public boolean hasCosmetic(UUID uuid, int id) {
        Connection connection = getConnection();
        if (connection == null) {
            Core.logInfo("Core > Unable to check cosmetic status - Cannot connect to MySql!");
            ErrorUtil.displayError("Unable to connect to MySQL!");
            return false;
        }

        boolean hasCosmetic = false;
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT status FROM cosmetics WHERE uuid=? AND cosmetic=?");
            statement.setString(1, uuid.toString());
            statement.setInt(2, id);
            ResultSet results = statement.executeQuery();
            hasCosmetic = results.next();

            statement.close();
            results.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            ErrorUtil.displayError(e);
        }
        return hasCosmetic;
    }

    public int getGameStat(GameType game, StatisticType type, CPlayer player) {
        return getGameStat(game, type, player.getUuid());
    }

    /**
     * Get a players statistic in a game
     *
     * @param game   the game to get the statistic from
     * @param type   the type of statistic to get
     * @param player the player to get the statistic from
     * @return the amount of the statistic they have
     */
    public int getGameStat(GameType game, StatisticType type, UUID player) {
        Connection connection = getConnection();
        if (connection == null) {
            Core.logInfo("Core > Unable to get game stats - Cannot connect to MySql!");
            ErrorUtil.displayError("Unable to connect to MySQL!");
            return 0;
        }

        int amount = 0;
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT amount FROM gameStatistics WHERE uuid=? AND type=? AND game=?");
            statement.setString(1, player.toString());
            statement.setString(2, type.getType());
            statement.setInt(3, game.getId());
            ResultSet results = statement.executeQuery();

            while (results.next()) {
                amount += results.getInt("amount");
            }

            statement.close();
            results.close();
            connection.close();
            return amount;
        } catch (SQLException e) {
            e.printStackTrace();
            ErrorUtil.displayError(e);
            return 0;
        }
    }

    /**
     * Add a game statistic to a player.
     *
     * @param game      the game that this happened in
     * @param statistic the statistic to add
     * @param amount    the amount to give the player
     * @param player    the player who earned this stat
     */
    public void addGameStat(GameType game, StatisticType statistic, int amount, CPlayer player) {
        addGameStat(game, statistic, amount, player.getUuid());
    }

    /**
     * Add a game statistic to a player
     *
     * @param game      the game that this happened in
     * @param statistic the statistic to add
     * @param player    the player who earned this stat
     * @param amount    the amount to give the player
     */
    public void addGameStat(GameType game, StatisticType statistic, int amount, UUID player) {
        Connection connection = getConnection();
        if (connection == null) {
            Core.logInfo("Core > Unable to add game stats - Cannot connect to MySql!");
            ErrorUtil.displayError("Unable to connect to MySQL!");
            return;
        }

        try {
            int finalAmount = getGameStat(game, statistic, player) + amount;
            PreparedStatement statement = connection.prepareStatement("INSERT INTO gameStatistics (uuid,type,game,amount) VALUES (?,?,?,?)");
            // Injections
            statement.setString(1, player.toString());
            statement.setString(2, statistic.getType());
            statement.setInt(3, game.getId());
            statement.setInt(4, finalAmount);

            statement.execute();

            // Finish up
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            ErrorUtil.displayError(e);
        }
    }

    /**
     * Get the honor mappings from mysql.
     *
     * @return the mappings
     */
    public List<HonorMapping> getHonorMappings() {
        Connection connection = getConnection();
        if (connection == null) {
            Core.logInfo("Core > Unable to get honor mappings - Cannot connect to MySQL!");
            ErrorUtil.displayError("Unable to connect to MySQL!");
            return new ArrayList<>();
        }
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM honorMappings");
            ResultSet results = statement.executeQuery();

            List<HonorMapping> result = new ArrayList<>();
            while (results.next()) {
                result.add(new HonorMapping(results.getInt("level"), results.getInt("honor")));
            }

            statement.close();
            results.close();
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            ErrorUtil.displayError(e);
            return new ArrayList<>();
        }
    }

    /**
     * Get honor leaderboard
     *
     * @param limit amount to get (max 10)
     * @return leaderboard map
     */
    public HashMap<Integer, TopHonorReport> getTopHonor(int limit) {
        HashMap<Integer, TopHonorReport> map = new HashMap<>();
        if (limit > 10) {
            limit = 10;
        }
        Connection connection = getConnection();
        if (connection == null) {
            Core.logInfo("Core > Unable to get top honor report - Cannot connect to MySQL!");
            ErrorUtil.displayError("Unable to connect to MySQL!");
            return map;
        }
        try {
            String statementText = "SELECT uuid,username,rank,honor FROM player_data ORDER BY honor DESC, username DESC LIMIT 0," + limit;
            PreparedStatement statement = connection.prepareStatement(statementText);
            ResultSet result = statement.executeQuery();
            int place = 1;
            while (result.next()) {
                TopHonorReport report = new TopHonorReport(UUID.fromString(result.getString("uuid")),
                        Rank.fromString(result.getString("rank")).getTagColor() +
                                result.getString("username"), place, result.getInt("honor"));
                map.put(place, report);
                ++place;
            }
            result.close();
            statement.close();
            return map;
        } catch (SQLException e) {
            e.printStackTrace();
            ErrorUtil.displayError(e);
        }
        return map;
    }

    /**
     * Get join report for UUID
     *
     * @param uuid the uuid
     * @return the join report
     */
    public JoinReport getJoinReport(UUID uuid) {
        Connection connection = getConnection();
        if (connection == null) {
            Core.logInfo("Core > Unable to get top honor report - Cannot connect to MySQL!");
            ErrorUtil.displayError("Unable to connect to MySQL!");
            return new JoinReport(uuid, Rank.SETTLER);
        }
        try {
            String statementText = "SELECT id,rank FROM player_data WHERE uuid=?";
            PreparedStatement statement = connection.prepareStatement(statementText);
            statement.setString(1, uuid.toString());
            ResultSet result = statement.executeQuery();
            if (!result.next()) {
                return new JoinReport(uuid, Rank.SETTLER);
            }
            JoinReport report = new JoinReport(uuid, Rank.fromString(result.getString("rank")));
            result.close();
            statement.close();
            return report;
        } catch (SQLException e) {
            e.printStackTrace();
            ErrorUtil.displayError(e);
        }
        return new JoinReport(uuid, Rank.SETTLER);
    }

}

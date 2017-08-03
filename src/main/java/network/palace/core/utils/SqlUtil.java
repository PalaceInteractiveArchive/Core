package network.palace.core.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import network.palace.core.Core;
import network.palace.core.honor.HonorMapping;
import network.palace.core.honor.TopHonorReport;
import network.palace.core.npc.mob.MobPlayerTexture;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.core.tracking.GameType;
import network.palace.core.tracking.StatisticType;
import org.bukkit.ChatColor;
import org.bukkit.Statistic;

import java.sql.*;
import java.util.*;

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

    /* Player Methods */

    /**
     * Get rank.
     *
     * @param uuid the uuid
     * @return the rank
     */
    public Rank getRank(UUID uuid) {
        Connection connection = getConnection();
        if (connection == null) {
            ErrorUtil.displayError("Unable to connect to MySQL!");
            Core.logInfo("Core > Could not get rank! - Cannot connect to MySQL!");
            return Rank.SETTLER;
        }
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT rank FROM player_data WHERE uuid=?");
            statement.setString(1, uuid.toString());
            ResultSet result = statement.executeQuery();
            if (!result.next()) {
                return Rank.SETTLER;
            }
            Rank rank = Rank.fromString(result.getString("rank"));
            result.close();
            statement.close();
            connection.close();
            return rank;
        } catch (SQLException e) {
            e.printStackTrace();
            ErrorUtil.displayError(e);
            return Rank.SETTLER;
        }
    }

    /**
     * Gets rank.
     *
     * @param username the username
     * @return the player's rank
     */
    public Rank getRank(String username) {
        Connection connection = getConnection();
        if (connection == null) {
            ErrorUtil.displayError("Unable to connect to MySQL!");
            Core.logInfo("Core > Could not get rank! - Cannot connect to MySQL!");
            return Rank.SETTLER;
        }
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT rank FROM player_data WHERE username=?");
            statement.setString(1, username);
            ResultSet result = statement.executeQuery();
            if (!result.next()) {
                return Rank.SETTLER;
            }
            Rank rank = Rank.fromString(result.getString("rank"));
            result.close();
            statement.close();
            connection.close();
            return rank;
        } catch (SQLException e) {
            e.printStackTrace();
            ErrorUtil.displayError(e);
            return Rank.SETTLER;
        }
    }

    /**
     * Player exists boolean.
     *
     * @param username the username
     * @return if the player exists or not
     */
    public boolean playerExists(String username) {
        Connection connection = getConnection();
        if (connection == null) {
            ErrorUtil.displayError("Unable to connect to MySQL!");
            Core.logInfo("Core > Could not check if player exists! - Cannot connect to MySQL!");
            return false;
        }
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT id FROM player_data WHERE username=?");
            statement.setString(1, username);
            ResultSet result = statement.executeQuery();
            boolean contains = result.next();
            result.close();
            statement.close();
            connection.close();
            return contains;
        } catch (SQLException e) {
            e.printStackTrace();
            ErrorUtil.displayError(e);
            return false;
        }
    }

    public int getIdFromName(String username) {
        Connection connection = getConnection();
        if (connection == null) {
            ErrorUtil.displayError("Unable to connect to MySQL!");
            Core.logInfo("Core > Could not get UUID from name! - Cannot connect to MySQL!");
            return 0;
        }
        try {
            PreparedStatement sql = connection.prepareStatement("SELECT id FROM player_data WHERE username=?");
            sql.setString(1, username);
            ResultSet result = sql.executeQuery();
            if (!result.next()) {
                result.close();
                sql.close();
                return 0;
            }
            int id = result.getInt("id");
            result.close();
            sql.close();
            connection.close();
            return id;
        } catch (SQLException e) {
            e.printStackTrace();
            ErrorUtil.displayError(e);
            return 0;
        }
    }

    /**
     * Gets unique id from name.
     *
     * @param username the username
     * @return the unique id from name
     */
    public UUID getUniqueIdFromName(String username) {
        Connection connection = getConnection();
        if (connection == null) {
            ErrorUtil.displayError("Unable to connect to MySQL!");
            Core.logInfo("Core > Could not get UUID from name! - Cannot connect to MySQL!");
            return UUID.randomUUID();
        }
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT uuid FROM player_data WHERE username=?");
            statement.setString(1, username);
            ResultSet result = statement.executeQuery();
            if (!result.next()) {
                result.close();
                statement.close();
                return null;
            }
            String uuid = result.getString("uuid");
            result.close();
            statement.close();
            connection.close();
            return UUID.fromString(uuid);
        } catch (SQLException e) {
            e.printStackTrace();
            ErrorUtil.displayError(e);
            return null;
        }
    }

    /* Permission Methods */

    /**
     * Gets permissions.
     *
     * @param rank the rank
     * @return the permissions
     */
    public Map<String, Boolean> getPermissions(Rank rank) {
        Connection connection = getConnection();
        if (connection == null) {
            ErrorUtil.displayError("Unable to connect to MySQL!");
            Core.logInfo("Core > Could not get permissions! - Cannot connect to MySQL!");
            return new HashMap<>();
        }
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM permissions WHERE rank=?");
            statement.setString(1, rank.getSqlName());
            ResultSet result = statement.executeQuery();
            Map<String, Boolean> permissions = new HashMap<>();
            while (result.next()) {
                permissions.put(result.getString("node"), result.getInt("value") == 1);
            }
            result.close();
            statement.close();
            connection.close();
            return permissions;
        } catch (SQLException e) {
            e.printStackTrace();
            ErrorUtil.displayError(e);
            return new HashMap<>();
        }
    }

    /**
     * Gets permissions.
     *
     * @param player the player
     * @return the permissions
     */
    public Map<String, Boolean> getPermissions(CPlayer player) {
        return getPermissions(player.getRank());
    }

    /**
     * Gets members.
     *
     * @param rank the rank
     * @return the members
     */
    public List<String> getMembers(Rank rank) {
        Connection connection = getConnection();
        if (connection == null) {
            ErrorUtil.displayError("Unable to connect to MySQL!");
            Core.logInfo("Core > Could not get members! - Cannot connect to MySQL!");
            return new ArrayList<>();
        }
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT username FROM player_data WHERE rank=?");
            statement.setString(1, rank.getSqlName());
            ResultSet result = statement.executeQuery();
            List<String> members = new ArrayList<>();
            while (result.next()) {
                members.add(result.getString("username"));
            }
            result.close();
            statement.close();
            connection.close();
            return members;
        } catch (SQLException e) {
            e.printStackTrace();
            ErrorUtil.displayError(e);
            return new ArrayList<>();
        }
    }

    /**
     * Sets rank.
     *
     * @param uuid the uuid
     * @param rank the rank
     */
    public void setRank(UUID uuid, Rank rank) {
        Connection connection = getConnection();
        if (connection == null) {
            ErrorUtil.displayError("Unable to connect to MySQL!");
            Core.logInfo("Core > Could not set player rank! - Cannot connect to MySQL!");
            return;
        }
        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE player_data SET rank=? WHERE uuid=?");
            statement.setString(1, rank.getSqlName());
            statement.setString(2, uuid.toString());
            statement.execute();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            ErrorUtil.displayError(e);
        }
    }

    /**
     * Sets permission.
     *
     * @param node  the node
     * @param rank  the rank
     * @param value the value
     */
    public void setPermission(String node, Rank rank, boolean value) {
        Connection connection = getConnection();
        if (connection == null) {
            Core.logInfo("Core > Could not set permission! - Cannot connect to MySQL!");
            ErrorUtil.displayError("Unable to connect to MySQL!");
            return;
        }
        try {
            String s = "IF EXISTS (SELECT * FROM permissions WHERE rank=? AND node=?) UPDATE permissions SET value=? WHERE node=? AND rank=? ELSE INSERT INTO Table1 VALUES (0,?,?,?)";
            PreparedStatement statement = connection.prepareStatement(s);
            statement.setString(1, rank.getSqlName());
            statement.setString(2, node);
            statement.setInt(3, value ? 1 : 0);
            statement.setString(4, node);
            statement.setString(5, rank.getSqlName());
            statement.setString(6, rank.getSqlName());
            statement.setString(7, node);
            statement.setInt(8, value ? 1 : 0);
            statement.execute();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            ErrorUtil.displayError(e);
        }
        Core.getPermissionManager().setPermission(rank, node, value);
    }

    /**
     * Unset permission.
     *
     * @param node the node
     * @param rank the rank
     */
    public void unsetPermission(String node, Rank rank) {
        Connection connection = getConnection();
        if (connection == null) {
            Core.logInfo("Core > Could not add permission! - Cannot connect to MySQL!");
            ErrorUtil.displayError("Unable to connect to MySQL!");
            return;
        }
        try {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM permissions WHERE rank=? AND node=?");
            statement.setString(1, rank.getSqlName());
            statement.setString(2, node);
            statement.execute();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            ErrorUtil.displayError(e);
        }
        Core.getPermissionManager().unsetPermission(rank, node);
    }

    /**
     * Give player Achievement
     *
     * @param player the player
     * @param id     achievement ID
     */
    public void addAchievement(CPlayer player, int id) {
        Connection connection = getConnection();
        if (connection == null) {
            Core.logInfo("Core > Could not add achievement! - Cannot connect to MySQL!");
            ErrorUtil.displayError("Unable to connect to MySQL!");
            return;
        }
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO achievements (uuid, achid, time) VALUES (?,?,?)");
            statement.setString(1, player.getUniqueId().toString());
            statement.setInt(2, id);
            statement.setInt(3, (int) (System.currentTimeMillis() / 1000));
            statement.execute();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            ErrorUtil.displayError(e);
        }
    }

    /**
     * Get all achievements for a player
     *
     * @param id    The player's internal id
     * @return list of the players achievements
     */
    public List<Integer> getAchievements(int id) {
        List<Integer> list = new ArrayList<>();
        Connection connection = getConnection();
        if (connection == null) {
            Core.logInfo("Core > Could not get achievements! - Cannot connect to MySQL!");
            ErrorUtil.displayError("Unable to connect to MySQL!");
            return list;
        }
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM achievements WHERE id=?");
            statement.setInt(1, id);
            ResultSet achResult = statement.executeQuery();
            while (achResult.next()) {
                list.add(achResult.getInt("achid"));
            }
            achResult.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            ErrorUtil.displayError(e);
        }
        return list;
    }

    /**
     * Cache a skin for later use
     *
     * @param uuid      UUID of the player
     * @param value     Value of the skin
     * @param signature Signature of the skin
     */
    public void cacheSkin(UUID uuid, String value, String signature) {
        Connection connection = getConnection();
        if (connection == null) {
            Core.logInfo("Core > Could not cache player hash! - Cannot connect to MySQL!");
            ErrorUtil.displayError("Unable to connect to MySQL!");
            return;
        }

        try {
            String statementText = "INSERT INTO skins (uuid,value,signature) VALUES (?,?,?) ON DUPLICATE KEY UPDATE value=?,signature=?";
            PreparedStatement statement = connection.prepareStatement(statementText);

            statement.setString(1, uuid.toString());
            statement.setString(2, value);
            statement.setString(3, signature);

            statement.setString(4, signature);
            statement.setString(5, uuid.toString());

            statement.execute();
            statement.close();

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            ErrorUtil.displayError(e);
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
     * @return        the amount of the statistic they have
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
     * @param player      the player who earned this stat
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
     * Add honor to a player
     * To remove honor, make amount negative
     *
     * @param id     the player's internal-id
     * @param amount the amount to add
     */
    public void addHonor(int id, int amount) {
        Connection connection = getConnection();
        if (connection == null) {
            Core.logInfo("Core > Unable to add honor - Cannot connect to MySQL!");
            ErrorUtil.displayError("Unable to connect to MySQL!");
            return;
        }
        try {
            String statementText = "UPDATE player_data SET honor=honor+? WHERE id=?";
            PreparedStatement statement = connection.prepareStatement(statementText);
            statement.setInt(1, amount);
            statement.setInt(2, id);
            statement.execute();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            ErrorUtil.displayError(e);
        }
    }

    /**
     * Set a player's honor
     *
     * @param id     the player's internal id
     * @param amount the amount
     */
    public void setHonor(int id, int amount) {
        Connection connection = getConnection();
        if (connection == null) {
            Core.logInfo("Core > Unable to set honor - Cannot connect to MySQL!");
            ErrorUtil.displayError("Unable to connect to MySQL!");
            return;
        }
        try {
            String statementText = "UPDATE player_data SET honor=? WHERE id=?";
            PreparedStatement statement = connection.prepareStatement(statementText);
            statement.setInt(1, amount);
            statement.setInt(2, id);
            statement.execute();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            ErrorUtil.displayError(e);
        }
    }

    /**
     * Get a player's honor
     *
     * @param id the player's id to get from
     * @return   the player's honor
     */
    public int getHonor(int id) {
        Connection connection = getConnection();
        if (connection == null) {
            Core.logInfo("Core > Unable to get honor - Cannot connect to MySQL!");
            ErrorUtil.displayError("Unable to connect to MySQL!");
            return 1;
        }

        try {
            PreparedStatement statement = connection.prepareStatement("SELECT honor FROM player_data WHERE id=?");
            statement.setInt(1, id);
            ResultSet results = statement.executeQuery();
            int amount = 0;
            while (results.next()) {
                amount = results.getInt("honor");
            }
            return amount;
        } catch (SQLException e) {
            e.printStackTrace();
            ErrorUtil.displayError(e);
            return 1;
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
            return new JoinReport(0, uuid, Rank.SETTLER);
        }
        try {
            String statementText = "SELECT id,rank FROM player_data WHERE uuid=?";
            PreparedStatement statement = connection.prepareStatement(statementText);
            statement.setString(1, uuid.toString());
            ResultSet result = statement.executeQuery();
            if (!result.next()) {
                return new JoinReport(0, uuid, Rank.SETTLER);
            }
            JoinReport report = new JoinReport(result.getInt("id"), uuid, Rank.fromString(result.getString("rank")));
            result.close();
            statement.close();
            return report;
        } catch (SQLException e) {
            e.printStackTrace();
            ErrorUtil.displayError(e);
        }
        return new JoinReport(0, uuid, Rank.SETTLER);
    }

    @AllArgsConstructor
    public class JoinReport {
        @Getter private final int sqlId;
        @Getter private final UUID uuid;
        @Getter private final Rank rank;
    }
}

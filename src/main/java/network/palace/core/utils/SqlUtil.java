package network.palace.core.utils;

import network.palace.core.Core;
import network.palace.core.npc.mob.MobPlayerTexture;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import org.bukkit.ChatColor;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * The type Sql util.
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
    public void loadLogin() {
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
        if (connection == null) return Rank.SETTLER;
        try {
            PreparedStatement sql = connection.prepareStatement("SELECT rank FROM player_data WHERE uuid=?");
            sql.setString(1, uuid.toString());
            ResultSet result = sql.executeQuery();
            if (!result.next()) {
                return Rank.SETTLER;
            }
            Rank rank = Rank.fromString(result.getString("rank"));
            result.close();
            sql.close();
            connection.close();
            return rank;
        } catch (SQLException e) {
            e.printStackTrace();
            return Rank.SETTLER;
        }
    }

    /**
     * Gets rank.
     *
     * @param username the username
     * @return the rank
     */
    public Rank getRank(String username) {
        Connection connection = getConnection();
        if (connection == null) return Rank.SETTLER;
        try {
            PreparedStatement sql = connection.prepareStatement("SELECT rank FROM player_data WHERE username=?");
            sql.setString(1, username);
            ResultSet result = sql.executeQuery();
            if (!result.next()) {
                return Rank.SETTLER;
            }
            Rank rank = Rank.fromString(result.getString("rank"));
            result.close();
            sql.close();
            connection.close();
            return rank;
        } catch (SQLException e) {
            e.printStackTrace();
            return Rank.SETTLER;
        }
    }

    /**
     * Player exists boolean.
     *
     * @param username the username
     * @return the boolean
     */
    public boolean playerExists(String username) {
        Connection connection = getConnection();
        if (connection == null) return false;
        try {
            PreparedStatement sql = connection.prepareStatement("SELECT id FROM player_data WHERE username=?");
            sql.setString(1, username);
            ResultSet result = sql.executeQuery();
            boolean contains = result.next();
            result.close();
            sql.close();
            connection.close();
            return contains;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
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
        if (connection == null) return UUID.randomUUID();
        try {
            PreparedStatement sql = connection.prepareStatement("SELECT uuid FROM player_data WHERE username=?");
            sql.setString(1, username);
            ResultSet result = sql.executeQuery();
            if (!result.next()) {
                result.close();
                sql.close();
                return null;
            }
            String uuid = result.getString("uuid");
            result.close();
            sql.close();
            connection.close();
            return UUID.fromString(uuid);
        } catch (SQLException e) {
            e.printStackTrace();
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
    public HashMap<String, Boolean> getPermissions(Rank rank) {
        Connection connection = getConnection();
        if (connection == null) return new HashMap<>();
        try {
            PreparedStatement sql = connection.prepareStatement("SELECT * FROM permissions WHERE rank=?");
            sql.setString(1, rank.getSqlName());
            ResultSet result = sql.executeQuery();
            HashMap<String, Boolean> permissions = new HashMap<>();
            while (result.next()) {
                permissions.put(result.getString("node"), result.getInt("value") == 1);
            }
            result.close();
            sql.close();
            connection.close();
            return permissions;
        } catch (SQLException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    /**
     * Gets permissions.
     *
     * @param player the player
     * @return the permissions
     */
    public HashMap<String, Boolean> getPermissions(CPlayer player) {
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
        if (connection == null) return new ArrayList<>();
        try {
            PreparedStatement sql = connection.prepareStatement("SELECT username FROM player_data WHERE rank=?");
            sql.setString(1, rank.getSqlName());
            ResultSet result = sql.executeQuery();
            List<String> members = new ArrayList<>();
            while (result.next()) {
                members.add(result.getString("username"));
            }
            result.close();
            sql.close();
            connection.close();
            return members;
        } catch (SQLException e) {
            e.printStackTrace();
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
        if (connection == null) return;
        try {
            PreparedStatement sql = connection.prepareStatement("UPDATE player_data SET rank=? WHERE uuid=?");
            sql.setString(1, rank.getSqlName());
            sql.setString(2, uuid.toString());
            sql.execute();
            sql.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
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
        if (connection == null) return;
        try {
            String s = "IF EXISTS (SELECT * FROM permissions WHERE rank=? AND node=?) UPDATE permissions SET value=? WHERE node=? AND rank=? ELSE INSERT INTO Table1 VALUES (0,?,?,?)";
            PreparedStatement sql = connection.prepareStatement(s);
            sql.setString(1, rank.getSqlName());
            sql.setString(2, node);
            sql.setInt(3, value ? 1 : 0);
            sql.setString(4, node);
            sql.setString(5, rank.getSqlName());
            sql.setString(6, rank.getSqlName());
            sql.setString(7, node);
            sql.setInt(8, value ? 1 : 0);
            sql.execute();
            sql.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
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
        if (connection == null) return;
        try {
            PreparedStatement sql = connection.prepareStatement("DELETE FROM permissions WHERE rank=? AND node=?");
            sql.setString(1, rank.getSqlName());
            sql.setString(2, node);
            sql.execute();
            sql.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
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
        if (connection == null) return;
        try {
            PreparedStatement sql = connection.prepareStatement("INSERT INTO achievements (uuid, achid, time) VALUES (?,?,?)");
            sql.setString(1, player.getUniqueId().toString());
            sql.setInt(2, id);
            sql.setInt(3, (int) (System.currentTimeMillis() / 1000));
            sql.execute();
            sql.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get all achievements for a player
     *
     * @param uuid The players uuid
     * @return list of the players achievements
     */
    public List<Integer> getAchievements(UUID uuid) {
        List<Integer> list = new ArrayList<>();
        Connection connection = getConnection();
        if (connection == null) return list;
        try {
            PreparedStatement ach = connection.prepareStatement("SELECT * FROM achievements WHERE uuid=?");
            ach.setString(1, uuid.toString());
            ResultSet achResult = ach.executeQuery();
            while (achResult.next()) {
                list.add(achResult.getInt("achid"));
            }
            achResult.close();
            ach.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Cache a skin for later use
     * @param uuid UUID of the player
     * @param value Value of the skin
     * @param signature Signature of the skin
     */
    public void cacheSkin(UUID uuid, String value, String signature) {
        Connection connection = getConnection();
        if (connection == null) return;

        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO skins (uuid, value, signature) VALUES (?,?,?)");

            statement.setString(1, uuid.toString());
            statement.setString(2, value);
            statement.setString(3, signature);

            statement.execute();
            statement.close();

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the cached skin for a player's uuid
     * @param uuid The uuid to find
     * @return The texture
     */
    public MobPlayerTexture getPlayerTextureHash(UUID uuid) {
        Connection connection = getConnection();
        MobPlayerTexture texture = new MobPlayerTexture("", "");

        if (connection == null) return texture;

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
        }
        return texture;
    }

    /**
     * Earn a cosmetic for a player
     *
     * @param player the player that earned it
     * @param id the id of the cosmetic that they earned
     */
    public void earnCosmetic(CPlayer player, int id) {
        Connection connection = getConnection();
        if (connection == null) {
            Core.logInfo("Core > Unable to give player cosmetic item - Cannot connect to MySql!");
            return;
        }

        try {
            // Create a new statement
            PreparedStatement statement = connection.prepareStatement("INSERT INTO caseEarnings (uuid,id,status) VALUES (?,?,?)");

            // Set the params and execute
            statement.setString(1, player.getUuid().toString());
            statement.setInt(2, id);
            statement.setBoolean(3, true);
            statement.execute();

            // Finish up
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Does a player have a cosmetic item?
     *
     * @param player the player to check
     * @param id the id to check
     * @return if the player has the cosmetic
     */
    public boolean hasCosmetic(CPlayer player, int id) {
        Connection connection = getConnection();
        if (connection == null) {
            Core.logInfo("Core > Unable to check cosmetic status - Cannot connect to MySql!");
            return false;
        }

        boolean hasCosmetic = false;
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT status FROM caseEarnings WHERE uuid=?,id=?,status=?");
            statement.setString(1, player.getUuid().toString());
            statement.setInt(2, id);
            statement.setBoolean(3, true);
            ResultSet results = statement.executeQuery();

            while (results.next()) {
                hasCosmetic = true;
            }

            statement.close();
            results.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return hasCosmetic;
    }
}

package network.palace.core.utils;

import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

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

    /**
     * Player Methods
     */

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

    public Rank getRank(String username) {
        try (Connection connection = getConnection()) {
            PreparedStatement sql = connection.prepareStatement("SELECT rank FROM player_data WHERE username=?");
            sql.setString(1, username);
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

    public boolean playerExists(String username) {
        try (Connection connection = getConnection()) {
            PreparedStatement sql = connection.prepareStatement("SELECT id FROM player_data WHERE username=?");
            sql.setString(1, username);
            ResultSet result = sql.executeQuery();
            boolean contains = result.next();
            result.close();
            sql.close();
            return contains;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public UUID getUniqueIdFromName(String username) {
        try (Connection connection = getConnection()) {
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
            return UUID.fromString(uuid);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Permission Methods
     */
    public HashMap<String, Boolean> getPermissions(Rank rank) {
        try (Connection connection = getConnection()) {
            PreparedStatement sql = connection.prepareStatement("SELECT * FROM permissions WHERE rank=?");
            sql.setString(1, rank.getSqlName());
            ResultSet result = sql.executeQuery();
            HashMap<String, Boolean> permissions = new HashMap<>();
            while (result.next()) {
                permissions.put(result.getString("node"), result.getInt("value") == 1);
            }
            result.close();
            sql.close();
            return permissions;
        } catch (SQLException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    public HashMap<String, Boolean> getPermissions(CPlayer player) {
        return getPermissions(player.getRank());
    }

    public List<String> getMembers(Rank rank) {
        try (Connection connection = getConnection()) {
            PreparedStatement sql = connection.prepareStatement("SELECT username FROM player_data WHERE rank=?");
            sql.setString(1, rank.getSqlName());
            ResultSet result = sql.executeQuery();
            List<String> members = new ArrayList<>();
            while (result.next()) {
                members.add(result.getString("username"));
            }
            result.close();
            sql.close();
            return members;
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void setRank(UUID uuid, Rank rank) {
        try (Connection connection = getConnection()) {
            PreparedStatement sql = connection.prepareStatement("UPDATE player_data SET rank=? WHERE uuid=?");
            sql.setString(1, rank.getSqlName());
            sql.setString(2, uuid.toString());
            sql.execute();
            sql.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setPermission(String node, Rank rank, boolean value) {
        try (Connection connection = getConnection()) {
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Core.getInstance().getPermissionManager().setPermission(rank, node, value);
    }

    public void unsetPermission(String node, Rank rank) {
        try (Connection connection = getConnection()) {
            PreparedStatement sql = connection.prepareStatement("DELETE FROM permissions WHERE rank=? AND node=?");
            sql.setString(1, rank.getSqlName());
            sql.setString(2, node);
            sql.execute();
            sql.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Core.getInstance().getPermissionManager().unsetPermission(rank, node);
    }
}

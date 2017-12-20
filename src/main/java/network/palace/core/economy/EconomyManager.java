package network.palace.core.economy;

import network.palace.core.Core;
import network.palace.core.events.EconomyUpdateEvent;
import network.palace.core.player.CPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

/**
 * The type Economy manager.
 */
public class EconomyManager {

    private final Map<UUID, Payment> balance = new HashMap<>();
    private final Map<UUID, Payment> tokens = new HashMap<>();

    /**
     * Instantiates a new Economy manager.
     */
    public EconomyManager() {
        Core.runTaskTimerAsynchronously(() -> {
            Map<UUID, Payment> localMap = new HashMap<>(balance);
            balance.clear();
            for (Map.Entry<UUID, Payment> entry : new HashSet<>(localMap.entrySet())) {
                Payment payment = new Payment(entry.getKey(), entry.getValue().getAmount(), entry.getValue().getSource());
                balance.remove(entry.getKey());
                if (payment.getAmount() == 0) continue;
                changeBalance(entry.getKey(), payment.getAmount(), payment.getSource());
            }
        }, 0L, 20L);
        Core.runTaskTimerAsynchronously(() -> {
            Map<UUID, Payment> localMap = new HashMap<>(tokens);
            tokens.clear();
            for (Map.Entry<UUID, Payment> entry : new HashSet<>(localMap.entrySet())) {
                Payment payment = new Payment(entry.getKey(), entry.getValue().getAmount(), entry.getValue().getSource());
                tokens.remove(entry.getKey());
                if (payment.getAmount() == 0) continue;
                changeTokens(entry.getKey(), payment.getAmount(), payment.getSource());
            }
        }, 0L, 20L);
    }

    private Map<UUID, Payment> getPartOfMap(Map<UUID, Payment> map, int amount) {
        if (map.size() <= amount) return new HashMap<>(map);
        Map<UUID, Payment> temp = new HashMap<>();
        int i = 0;
        for (Map.Entry<UUID, Payment> entry : map.entrySet()) {
            if (i >= amount) {
                break;
            }
            temp.put(entry.getKey(), entry.getValue());
            i++;
        }
        return temp;
    }

    private void changeBalance(UUID uuid, int amount, String source) {
        Connection connection = Core.getSqlUtil().getConnection();
        if (connection == null) return;
        try {
            PreparedStatement sql = connection.prepareStatement("UPDATE player_data SET balance=(balance+?) WHERE uuid=?;");
            sql.setInt(1, amount);
            sql.setString(2, uuid.toString());
            sql.execute();
            sql.close();
            logTransaction(uuid, amount, source, "add balance");
            new EconomyUpdateEvent(uuid, getBalance(uuid), true).call();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void changeTokens(UUID uuid, int amount, String source) {
        Connection connection = Core.getSqlUtil().getConnection();
        if (connection == null) return;
        try {
            PreparedStatement sql = connection.prepareStatement("UPDATE player_data SET tokens=(tokens+?) WHERE uuid=?;");
            sql.setInt(1, amount);
            sql.setString(2, uuid.toString());
            sql.execute();
            sql.close();
            logTransaction(uuid, amount, source, "add tokens");
            new EconomyUpdateEvent(uuid, getTokens(uuid), false).call();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets balance.
     *
     * @param uuid   the uuid
     * @param amount the amount
     * @param source the source
     * @param set    the set
     */
    public void setBalance(UUID uuid, int amount, String source, boolean set) {
        Connection connection = Core.getSqlUtil().getConnection();
        if (connection == null) return;
        try {
            PreparedStatement sql = connection.prepareStatement("UPDATE player_data SET balance=? WHERE uuid=?;");
            sql.setInt(1, amount);
            sql.setString(2, uuid.toString());
            sql.execute();
            sql.close();
            new EconomyUpdateEvent(uuid, getBalance(uuid), true).call();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (set) {
            logTransaction(uuid, amount, source, "set balance");
        }
    }

    /**
     * Sets tokens.
     *
     * @param uuid   the uuid
     * @param amount the amount
     * @param source the source
     * @param set    the set
     */
    public void setTokens(UUID uuid, int amount, String source, boolean set) {
        Connection connection = Core.getSqlUtil().getConnection();
        if (connection == null) return;
        try {
            PreparedStatement sql = connection.prepareStatement("UPDATE player_data SET tokens=? WHERE uuid=?;");
            sql.setInt(1, amount);
            sql.setString(2, uuid.toString());
            sql.execute();
            sql.close();
            new EconomyUpdateEvent(uuid, getTokens(uuid), false).call();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (set) {
            logTransaction(uuid, amount, source, "set tokens");
        }
    }

    /**
     * Logout.
     *
     * @param uuid the uuid
     */
    public void logout(UUID uuid) {
        if (balance.containsKey(uuid)) {
            Payment payment = balance.remove(uuid);
            changeBalance(uuid, payment.getAmount(), payment.getSource());
        }
        if (tokens.containsKey(uuid)) {
            Payment payment = tokens.remove(uuid);
            changeTokens(uuid, payment.getAmount(), payment.getSource());
        }
    }

    /**
     * Gets balance.
     *
     * @param requester the requester
     * @param name      the name
     * @return the balance
     */
    public int getBalance(CommandSender requester, String name) {
        Connection connection = Core.getSqlUtil().getConnection();
        if (connection == null) return 0;
        try {
            PreparedStatement sql = connection.prepareStatement("SELECT balance FROM player_data WHERE username=?");
            sql.setString(1, name);
            ResultSet result = sql.executeQuery();
            int amount = 0;
            int times = 0;
            while (result.next()) {
                times++;
                amount = result.getInt("balance");
            }
            if (times > 1) {
                requester.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "More than one value found for " +
                        ChatColor.GREEN + name + "!");
            }
            result.close();
            sql.close();
            return amount;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Gets balance.
     *
     * @param uuid the uuid
     * @return the balance
     */
    public int getBalance(UUID uuid) {
        Connection connection = Core.getSqlUtil().getConnection();
        if (connection == null) return 0;
        try {
            PreparedStatement sql = connection.prepareStatement("SELECT balance FROM player_data WHERE uuid=?");
            sql.setString(1, uuid.toString());
            ResultSet result = sql.executeQuery();
            if (!result.next()) {
                result.close();
                sql.close();
                return 0;
            }
            int balance = result.getInt("balance");
            result.close();
            sql.close();
            return balance;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Gets tokens.
     *
     * @param requester the requester
     * @param name      the name
     * @return the tokens
     */
    public int getTokens(CommandSender requester, String name) {
        Connection connection = Core.getSqlUtil().getConnection();
        if (connection == null) return 0;
        try {
            PreparedStatement sql = connection.prepareStatement("SELECT tokens FROM player_data WHERE username=?");
            sql.setString(1, name);
            ResultSet result = sql.executeQuery();
            int amount = 0;
            int times = 0;
            while (result.next()) {
                times++;
                amount = result.getInt("tokens");
            }
            if (times > 1) {
                requester.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "More than one value found for " +
                        ChatColor.GREEN + name + "!");
            }
            result.close();
            sql.close();
            return amount;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Gets tokens.
     *
     * @param uuid the uuid
     * @return the tokens
     */
    public int getTokens(UUID uuid) {
        Connection connection = Core.getSqlUtil().getConnection();
        if (connection == null) return 0;
        try {
            PreparedStatement sql = connection.prepareStatement("SELECT tokens FROM player_data WHERE uuid=?");
            sql.setString(1, uuid.toString());
            ResultSet result = sql.executeQuery();
            if (!result.next()) {
                result.close();
                sql.close();
                return 0;
            }
            int tokens = result.getInt("tokens");
            result.close();
            sql.close();
            return tokens;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Add balance.
     *
     * @param uuid   the uuid
     * @param amount the amount
     */
    public void addBalance(UUID uuid, int amount) {
        addBalance(uuid, amount, "plugin");
    }

    /**
     * Add balance.
     *
     * @param uuid   the uuid
     * @param amount the amount
     * @param source the source
     */
    public void addBalance(UUID uuid, int amount, String source) {
        String sign = amount > 0 ? "+ " : "- ";
        CPlayer player = Core.getPlayerManager().getPlayer(uuid);
        player.getActionBar().show(ChatColor.YELLOW + sign + "$" + Math.abs(amount));
        addCurrency(uuid, amount, source, balance);
    }

    /**
     * Add tokens.
     *
     * @param uuid   the uuid
     * @param amount the amount
     */
    public void addTokens(UUID uuid, int amount) {
        addTokens(uuid, amount, "plugin");
    }

    /**
     * Add tokens.
     *
     * @param uuid   the uuid
     * @param amount the amount
     * @param source the source
     */
    public void addTokens(UUID uuid, int amount, String source) {
        String sign = amount > 0 ? "+ " : "- ";
        CPlayer player = Core.getPlayerManager().getPlayer(uuid);
        player.getActionBar().show(ChatColor.YELLOW + sign + Math.abs(amount) + " Tokens");
        addCurrency(uuid, amount, source, tokens);
    }

    /**
     * Method that works for both balance and tokens
     *
     * @param uuid   the uuid
     * @param amount the amount
     * @param source the source
     * @param map    the map to add the payment to
     */
    private void addCurrency(UUID uuid, int amount, String source, Map<UUID, Payment> map) {
        if (map.containsKey(uuid)) {
            Payment payment = map.remove(uuid);
            if (amount + payment.getAmount() == 0) {
                return;
            }
            map.put(uuid, new Payment(uuid, amount + payment.getAmount(), source));
        } else {
            map.put(uuid, new Payment(uuid, amount, source));
        }
    }

    private void logTransaction(UUID uuid, int amount, String source, String type) {
        if (type.length() > 15) {
            return;
        }
        Connection connection = Core.getSqlUtil().getConnection();
        if (connection == null) return;
        try {
            PreparedStatement sql = connection.prepareStatement("INSERT INTO economy_logs (uuid, amount, type, source, server, timestamp)" +
                    " VALUES ('" + uuid.toString() + "', '" + amount + "', '" + type + "', '" + source + "', '" +
                    Core.getInstanceName() + "', '" + System.currentTimeMillis() / 1000 + "')");
            sql.execute();
            sql.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

package network.palace.core.resource;

import com.comphenix.protocol.PacketType;
import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import org.bukkit.ChatColor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * The type Resource manager.
 */
public class ResourceManager {

    private HashMap<String, ResourcePack> packs = new HashMap<>();
    private boolean first = true;
    private HashMap<UUID, String> downloading = new HashMap<>();

    /**
     * Instantiates a new Resource manager.
     */
    public ResourceManager() {
        try {
            initialize();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initialize.
     *
     * @throws SQLException the sql exception
     */
    public void initialize() throws SQLException {
        packs.clear();
        Connection connection = Core.getSqlUtil().getConnection();
        if (connection == null) return;
        PreparedStatement sql = connection.prepareStatement("SELECT * FROM resource_packs");
        ResultSet result = sql.executeQuery();
        while (result.next()) {
            packs.put(result.getString("name"), new ResourcePack(result.getString("name"), result.getString("url")));
        }
        result.close();
        sql.close();
        if (first) {
            Core.addPacketListener(new ResourceListener(Core.getInstance(), PacketType.Play.Client.RESOURCE_PACK_STATUS));
            first = false;
        }
    }

    /**
     * Downloading result.
     *
     * @param uuid   the uuid
     * @param status the status
     */
    public void downloadingResult(UUID uuid, PackStatus status) {
        String pack = downloading.remove(uuid);
        if (status != null) {
            switch (status) {
                case LOADED: {
                    CPlayer player = Core.getPlayerManager().getPlayer(uuid);
                    if (pack.equalsIgnoreCase("blank")) {
                        pack = "none";
                    }
                    setCurrentPack(player, pack);
                    break;
                }
                case FAILED:
                case DECLINED: {
                    CPlayer player = Core.getPlayerManager().getPlayer(uuid);
                    if (pack.equalsIgnoreCase("blank")) {
                        setCurrentPack(player, "none");
                    }
                    break;
                }
            }
        }
    }

    /**
     * Gets packs.
     *
     * @return the packs
     */
    public List<ResourcePack> getPacks() {
        return new ArrayList<>(packs.values());
    }

    /**
     * Gets pack.
     *
     * @param name the name
     * @return the pack
     */
    public ResourcePack getPack(String name) {
        return packs.get(name);
    }

    /**
     * Send pack.
     *
     * @param player the player
     * @param pack   the pack
     */
    public void sendPack(CPlayer player, ResourcePack pack) {
        player.sendMessage(ChatColor.GREEN + "Attempting to send you the " + ChatColor.YELLOW + pack.getName() +
                ChatColor.GREEN + " Resource Pack!");
        player.getResourcePack().send(pack.getUrl(), "null");
        downloading.put(player.getUniqueId(), pack.getName());

    }

    /**
     * Sets current pack.
     *
     * @param player the player
     * @param pack   the pack
     */
    public void setCurrentPack(CPlayer player, String pack) {
        if (player == null) return;
        player.setPack(pack);
//        PacketSetPack packet = new PacketSetPack(player.getUniqueId(), pack);
//        Core.getDashboardConnection().send(packet);
    }

    /**
     * Send pack.
     *
     * @param player the player
     * @param name   the name
     */
    public void sendPack(CPlayer player, String name) {
        ResourcePack pack = getPack(name);
        if (pack == null) {
            player.sendMessage(ChatColor.RED + "We tried to send you a Resource Pack, but it was not found!");
            player.sendMessage(ChatColor.RED + "Please contact a Staff Member about this. (Error Code 101)");
            return;
        }
        sendPack(player, pack);
    }

    /**
     * Reload.
     */
    public void reload() {
        packs.clear();
        try {
            initialize();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

package network.palace.core.resource;

import com.comphenix.protocol.PacketType;
import network.palace.core.Core;
import network.palace.core.dashboard.packets.dashboard.PacketSetPack;
import network.palace.core.player.CPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * The type Resource manager.
 */
public class ResourceManager {

    private final Map<String, ResourcePack> packs = new HashMap<>();
    private boolean first = true;
    private final Map<UUID, String> downloading = new HashMap<>();

    /**
     * Instantiates a new Resource manager.
     */
    public ResourceManager() {
        initialize();
    }

    /**
     * Initialize SQL connections
     */
    private void initialize() {
        packs.clear();
        if (Core.isDashboardAndSqlDisabled()) return;
        try (Connection connection = Core.getSqlUtil().getConnection()) {
            PreparedStatement sql = connection.prepareStatement("SELECT * FROM resource_packs");
            ResultSet result = sql.executeQuery();
            while (result.next()) {
                packs.put(result.getString("name"), new ResourcePack(result.getString("name"),
                        result.getString("url"), result.getString("hash")));
            }
            result.close();
            sql.close();
            if (first) {
                Core.addPacketListener(new ResourceListener(Core.getInstance(), PacketType.Play.Client.RESOURCE_PACK_STATUS));
                first = false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
        if (pack == null) {
            return;
        }
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
        if (player.getOnlineTime() < 2000) {
            Bukkit.getScheduler().runTaskLater(Core.getInstance(), () -> {
                player.sendMessage(ChatColor.GREEN + "Attempting to send you the " + ChatColor.YELLOW + pack.getName() +
                        ChatColor.GREEN + " Resource Pack!");
                downloading.put(player.getUniqueId(), pack.getName());
                player.getResourcePack().send(pack.getUrl(), pack.getHash().trim().equals("") ? "null" : pack.getHash());
            }, 20L);
            return;
        }
        player.sendMessage(ChatColor.GREEN + "Attempting to send you the " + ChatColor.YELLOW + pack.getName() +
                ChatColor.GREEN + " Resource Pack!");
        downloading.put(player.getUniqueId(), pack.getName());
        player.getResourcePack().send(pack.getUrl(), pack.getHash().trim().equals("") ? "null" : pack.getHash());
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
        PacketSetPack packet = new PacketSetPack(player.getUniqueId(), pack);
        Core.getDashboardConnection().send(packet);
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
        initialize();
    }
}

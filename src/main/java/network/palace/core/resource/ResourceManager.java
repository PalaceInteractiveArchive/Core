package network.palace.core.resource;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import network.palace.core.Core;
import network.palace.core.dashboard.packets.dashboard.PacketSetPack;
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
 * Created by Marc on 3/17/15
 */
public class ResourceManager {
    private HashMap<String, ResourcePack> packs = new HashMap<>();
    private boolean first = true;
    private HashMap<UUID, String> downloading = new HashMap<>();

    public ResourceManager() {
        try {
            initialize();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void initialize() throws SQLException {
        packs.clear();
        if (Core.getSqlUtil().getConnection() == null) return;
        Connection connection = Core.getSqlUtil().getConnection();
        PreparedStatement sql = connection.prepareStatement("SELECT * FROM resource_packs");
        ResultSet result = sql.executeQuery();
        while (result.next()) {
            packs.put(result.getString("name"), new ResourcePack(result.getString("name"), result.getString("url")));
        }
        result.close();
        sql.close();
        if (first) {
            ProtocolLibrary.getProtocolManager().addPacketListener(new ResourceListener(Core.getInstance(),
                    PacketType.Play.Client.RESOURCE_PACK_STATUS));
            first = false;
        }
    }

    public void downloadingResult(UUID uuid, PackStatus status) {
        if (status != null) {
            switch (status) {
                case LOADED:
                    String pack = downloading.remove(uuid);
                    CPlayer player = Core.getPlayerManager().getPlayer(uuid);
                    setCurrentPack(player, pack);
                    break;
            }
        }
        downloading.remove(uuid);
    }

    public List<ResourcePack> getPacks() {
        return new ArrayList<>(packs.values());
    }

    public ResourcePack getPack(String name) {
        return packs.get(name);
    }

    public void sendPack(CPlayer player, ResourcePack pack) {
        player.sendMessage(ChatColor.GREEN + "Attempting to send you the " + ChatColor.YELLOW + pack.getName() +
                ChatColor.GREEN + " Resource Pack!");
        player.getResourcePack().send(pack.getUrl(), "null");
        downloading.put(player.getUniqueId(), pack.getName());

    }

    public void setCurrentPack(CPlayer player, String pack) {
        if (player == null) return;
        player.setPack(pack);
        PacketSetPack packet = new PacketSetPack(player.getUniqueId(), pack);
        Core.getInstance().getDashboardConnection().send(packet);
    }

    public void sendPack(CPlayer player, String name) {
        ResourcePack pack = getPack(name);
        if (pack == null) {
            player.sendMessage(ChatColor.RED + "We tried to send you a Resource Pack, but it was not found!");
            player.sendMessage(ChatColor.RED + "Please contact a Staff Member about this. (Error Code 101)");
            return;
        }
        sendPack(player, pack);
    }

    public void reload() {
        packs.clear();
        try {
            initialize();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

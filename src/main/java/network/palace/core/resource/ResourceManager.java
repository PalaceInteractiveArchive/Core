package network.palace.core.resource;

import com.comphenix.protocol.PacketType;
import network.palace.core.Core;
import network.palace.core.dashboard.packets.dashboard.PacketSetPack;
import network.palace.core.player.CPlayer;
import org.bukkit.ChatColor;

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
        List<ResourcePack> list = Core.getMongoHandler().getResourcePacks();
        for (ResourcePack pack : list) {
            packs.put(pack.getName(), pack);
        }
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
        if (pack == null) {
            return;
        }
        CPlayer player = Core.getPlayerManager().getPlayer(uuid);
        player.getTitle().show("", "");
        if (status != null) {
            switch (status) {
                case LOADED: {
                    if (pack.equalsIgnoreCase("blank")) {
                        pack = "none";
                    }
                    setCurrentPack(player, pack);
                    break;
                }
                case FAILED:
                case DECLINED: {
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
            Core.runTaskLater(() -> sendPackNoDelay(player, pack), 40L);
            return;
        }
        sendPackNoDelay(player, pack);
    }

    private void sendPackNoDelay(CPlayer player, ResourcePack pack) {
        player.sendMessage(ChatColor.GREEN + "Attempting to send you the " + ChatColor.YELLOW + pack.getName() +
                ChatColor.GREEN + " Resource Pack!");
        player.getTitle().show("Sending Resource Pack", "This might take up to 30 seconds...", 0, 1200, 0);
        downloading.put(player.getUniqueId(), pack.getName());

        pack.sendTo(player);
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

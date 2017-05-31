package network.palace.core.resource;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

/**
 * The type Resource listener.
 */
public class ResourceListener extends PacketAdapter {

    /**
     * Instantiates a new Resource listener.
     *
     * @param plugin the plugin
     * @param types  the types
     */
    public ResourceListener(Plugin plugin, PacketType... types) {
        super(plugin, types);
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        if (event.getPlayer() == null) return;
        CPlayer player = Core.getPlayerManager().getPlayer(event.getPlayer());
        try {
            PacketContainer packet = event.getPacket();
            EnumWrappers.ResourcePackStatus status = packet.getResourcePackStatus().read(0);
            switch (status) {
                case SUCCESSFULLY_LOADED:
                    new ResourceStatusEvent(PackStatus.LOADED, player).call();
                    Core.getResourceManager().downloadingResult(player.getUniqueId(), PackStatus.LOADED);
                    return;
                case DECLINED:
                    new ResourceStatusEvent(PackStatus.DECLINED, player).call();
                    Core.getResourceManager().downloadingResult(player.getUniqueId(), PackStatus.DECLINED);
                    return;
                case FAILED_DOWNLOAD:
                    new ResourceStatusEvent(PackStatus.FAILED, player).call();
                    Core.getResourceManager().downloadingResult(player.getUniqueId(), PackStatus.FAILED);
                    return;
                case ACCEPTED:
                    new ResourceStatusEvent(PackStatus.ACCEPTED, player).call();
                    return;
                default:
                    Core.getResourceManager().downloadingResult(player.getUniqueId(), null);
                    player.sendMessage(ChatColor.RED + "There seems to be an error, please report this to a Staff Member! (Error Code 100)");
            }
        } catch (Exception e) {
            player.sendMessage(ChatColor.RED + "There seems to be an error, please report this to a Staff Member! (Error Code 100)");
            e.printStackTrace();
        }
    }
}

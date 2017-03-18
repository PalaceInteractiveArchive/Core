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
        CPlayer tp = Core.getPlayerManager().getPlayer(event.getPlayer());
        try {
            PacketContainer packet = event.getPacket();
            EnumWrappers.ResourcePackStatus status = packet.getResourcePackStatus().read(0);
            switch (status) {
                case SUCCESSFULLY_LOADED:
                    new ResourceStatusEvent(PackStatus.LOADED, tp).call();
                    Core.getResourceManager().downloadingResult(tp.getUniqueId(), PackStatus.LOADED);
                    return;
                case DECLINED:
                    new ResourceStatusEvent(PackStatus.DECLINED, tp).call();
                    Core.getResourceManager().downloadingResult(tp.getUniqueId(), PackStatus.DECLINED);
                    return;
                case FAILED_DOWNLOAD:
                    new ResourceStatusEvent(PackStatus.FAILED, tp).call();
                    Core.getResourceManager().downloadingResult(tp.getUniqueId(), PackStatus.FAILED);
                    return;
                case ACCEPTED:
                    new ResourceStatusEvent(PackStatus.ACCEPTED, tp).call();
                    return;
                default:
                    Core.getResourceManager().downloadingResult(tp.getUniqueId(), null);
                    tp.sendMessage(ChatColor.RED + "There seems to be an error, please report this to a Staff Member! (Error Code 100)");
            }
        } catch (Exception e) {
            tp.sendMessage(ChatColor.RED + "There seems to be an error, please report this to a Staff Member! (Error Code 100)");
            e.printStackTrace();
        }
    }
}

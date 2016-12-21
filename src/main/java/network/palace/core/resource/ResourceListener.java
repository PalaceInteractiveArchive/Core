package network.palace.core.resource;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

/**
 * Created by Marc on 3/6/15
 */
public class ResourceListener extends PacketAdapter {

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
                    Bukkit.getPluginManager().callEvent(new ResourceStatusEvent(PackStatus.LOADED, tp));
                    Core.getResourceManager().downloadingResult(tp.getUniqueId(), PackStatus.LOADED);
                    return;
                case DECLINED:
                    Bukkit.getPluginManager().callEvent(new ResourceStatusEvent(PackStatus.DECLINED, tp));
                    Core.getResourceManager().downloadingResult(tp.getUniqueId(), PackStatus.DECLINED);
                    return;
                case FAILED_DOWNLOAD:
                    Bukkit.getPluginManager().callEvent(new ResourceStatusEvent(PackStatus.FAILED, tp));
                    Core.getResourceManager().downloadingResult(tp.getUniqueId(), PackStatus.FAILED);
                    return;
                case ACCEPTED:
                    Bukkit.getPluginManager().callEvent(new ResourceStatusEvent(PackStatus.ACCEPTED, tp));
                    return;
                default:
                    Core.getResourceManager().downloadingResult(tp.getUniqueId(), null);
                    tp.sendMessage(ChatColor.RED + "There seems to be an Error, please report this to a Staff Member! (Error Code 100)");
            }
        } catch (Exception e) {
            tp.sendMessage(ChatColor.RED + "There seems to be an Error, please report this to a Staff Member! (Error Code 100)");
            e.printStackTrace();
        }
    }
}

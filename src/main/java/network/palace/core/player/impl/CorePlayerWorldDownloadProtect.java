package network.palace.core.player.impl;

import network.palace.core.Core;
import network.palace.core.dashboard.packets.dashboard.PacketWDLProtect;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class CorePlayerWorldDownloadProtect implements PluginMessageListener {

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] data) {
        if (channel.equals("WDL|INIT")) {
            player.sendMessage(ChatColor.RED + "Palace Network does not authorize the use of World Downloader Mods!");
            PacketWDLProtect packet = new PacketWDLProtect(player.getUniqueId());
            Core.getInstance().getDashboardConnection().send(packet);
        }
    }
}

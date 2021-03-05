package network.palace.core.player.impl;

import network.palace.core.Core;
import network.palace.core.messagequeue.packets.KickPlayerPacket;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

/**
 * The type Core player world download protect.
 */
public class CorePlayerWorldDownloadProtect implements PluginMessageListener {

    @Override
    public void onPluginMessageReceived(String channel, Player pl, byte[] data) {
        CPlayer player = Core.getPlayerManager().getPlayer(pl);
        if (player == null) return;
        if (channel.equals("WDL|INIT")) {
            if (!player.getRegistry().hasEntry("wdl_bypass")) {
                if (player.getRank().getRankId() >= Rank.TRAINEE.getRankId()) {
                    Document doc = Core.getMongoHandler().getPlayer(player.getUniqueId(), new Document("wdl_bypass", true));
                    if (doc != null && doc.containsKey("wdl_bypass")) {
                        boolean bypass = doc.getBoolean("wdl_bypass");
                        player.getRegistry().addEntry("wdl_bypass", bypass);
                    } else {
                        player.getRegistry().addEntry("wdl_bypass", false);
                    }
                } else {
                    player.getRegistry().addEntry("wdl_bypass", false);
                }
            }

            if ((boolean) player.getRegistry().getEntry("wdl_bypass")) return;

            long expires = System.currentTimeMillis() + (3 * 24 * 60 * 60 * 1000);
            Core.getMongoHandler().banPlayer(player.getUniqueId(), "Attempting to use a World Downloader",
                    expires, false, "Core-WDLProtect");
            try {
                Core.getMessageHandler().sendMessage(new KickPlayerPacket(player.getUniqueId(),
                        ChatColor.RED + "You are temporarily banned from this server!\n\n" +
                                ChatColor.YELLOW + "Reason: " + ChatColor.WHITE + "Attempting to use a World Downloader" + "\n\n" +
                                ChatColor.YELLOW + "Expires: " + ChatColor.WHITE + "3 Days\n\n" +
                                ChatColor.YELLOW + "Appeal at " + ChatColor.AQUA + "" + ChatColor.UNDERLINE + "https://palnet.us/appeal",
                        false), Core.getMessageHandler().ALL_PROXIES);
                Core.getMessageHandler().sendStaffMessage(ChatColor.GREEN + player.getName() + ChatColor.RED + " was banned by " +
                        ChatColor.GREEN + Core.getInstanceName() + ChatColor.RED + " Reason: " + ChatColor.GREEN + "Attempting to use a World Downloader" +
                        ChatColor.RED + " Expires: " + ChatColor.GREEN + "3 Days");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

package com.palacemc.tests;

import com.palacemc.core.Core;
import com.palacemc.core.events.CorePlayerJoinDelayedEvent;
import com.palacemc.core.player.CPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class TestsListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoinDelayed(CorePlayerJoinDelayedEvent event) {
        CPlayer player = event.getPlayer();
        // Scoreboard
        player.getScoreboard().setTitle(ChatColor.GOLD + "Scoreboard Test");
        player.getScoreboard().setLine(0, "");
        player.getScoreboard().setLine(1, ChatColor.DARK_AQUA + "Hello " + event.getPlayer().getName());
        player.getScoreboard().setLine(2, "");
        // Boss bar
        player.getBossBar().setEverything(ChatColor.GOLD + "Boss Bar Test", 0.5, BarColor.BLUE, BarStyle.SEGMENTED_6);
        // Tablist header and footer
        player.getHeaderFooter().setHeader(ChatColor.GOLD + "Header Test");
        player.getHeaderFooter().setFooter(ChatColor.DARK_AQUA + "Footer Test");
        // Action bar
        player.getActionBar().show(ChatColor.GOLD + "Action Bar Test");
        // Title and subtitle
        player.getTitle().show(ChatColor.GOLD + "Title Test", ChatColor.DARK_AQUA + "Subtitle Test");
        // Test Format Messages
        TestsMain plugin = Core.getPluginInstance(TestsMain.class);
        player.sendFormatMessage(plugin, "test");
    }
}

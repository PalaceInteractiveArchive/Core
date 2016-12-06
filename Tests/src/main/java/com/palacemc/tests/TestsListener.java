package com.palacemc.tests;

import com.thepalace.core.events.CorePlayerJoinDelayedEvent;
import com.thepalace.core.player.CPlayer;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class TestsListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoinDelayed(CorePlayerJoinDelayedEvent event) {
        CPlayer player = event.getPlayer();
        // Scoreboard
        String hello = ChatColor.DARK_AQUA + "Hello " + event.getPlayer().getName();
        player.getScoreboard().title(ChatColor.GOLD + "Scoreboard Test").set(100, "TEST").setBlank(2).set(1, hello).setBlank(0);
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
        TestsMain plugin = TestsMain.getPlugin(TestsMain.class);
        player.sendFormatMessage(plugin, "test");
    }
}

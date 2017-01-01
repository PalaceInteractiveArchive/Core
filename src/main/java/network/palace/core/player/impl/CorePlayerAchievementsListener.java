package network.palace.core.player.impl;

import network.palace.core.Core;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAchievementAwardedEvent;

public class CorePlayerAchievementsListener implements Listener {

    public CorePlayerAchievementsListener() {
        Core.registerListener(this);
    }

    @EventHandler
    public void onPlayerAchievementAwarded(PlayerAchievementAwardedEvent event) {
        event.setCancelled(true);
    }
}

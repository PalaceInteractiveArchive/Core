package network.palace.core.player.impl;

import network.palace.core.Core;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class CorePlayerPickupItemListener implements Listener {

    public CorePlayerPickupItemListener() {
        Core.registerListener(this);
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        if (event.getItem().hasMetadata("special")) {
            if (event.getItem().getMetadata("special").get(0).asBoolean()) {
                event.setCancelled(true);
            }
        }
    }
}

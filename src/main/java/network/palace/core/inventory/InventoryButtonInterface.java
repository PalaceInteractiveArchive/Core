package network.palace.core.inventory;

import network.palace.core.player.CPlayer;
import org.bukkit.inventory.ItemStack;

public interface InventoryButtonInterface {

    ItemStack getStack();
    InventoryClick getClick();
    boolean isVisible(CPlayer player);

}

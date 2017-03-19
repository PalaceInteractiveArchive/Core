package network.palace.core.inventory;

import network.palace.core.player.CPlayer;

public interface InventoryClick {

    void onPlayerClick(CPlayer player, ClickAction action);

}

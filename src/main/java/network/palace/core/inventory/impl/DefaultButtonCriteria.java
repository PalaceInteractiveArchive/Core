package network.palace.core.inventory.impl;

import network.palace.core.inventory.ButtonCriteria;
import network.palace.core.player.CPlayer;

/**
 * Created by Marc on 4/3/17.
 */
public class DefaultButtonCriteria implements ButtonCriteria {

    @Override
    public boolean isVisible(CPlayer player) {
        return true;
    }
}

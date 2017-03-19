package network.palace.core.inventory;

import org.bukkit.event.inventory.ClickType;

public enum ClickAction {

    LEFT,
    MIDDLE,
    RIGHT;

    public static ClickAction getActionTypeFor(ClickType click) {
        switch (click) {
            case RIGHT:
            case SHIFT_RIGHT:
                return ClickAction.RIGHT;
            case MIDDLE:
                return ClickAction.MIDDLE;
            default:
                return ClickAction.LEFT;
        }
    }
}

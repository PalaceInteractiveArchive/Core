package network.palace.core.pathfinding.npc;

import com.comphenix.protocol.wrappers.EnumWrappers;

/**
 * @author Innectic
 * @since 1/21/2017
 */
public enum ClickAction {
    RIGHT_CLICK,
    LEFT_CLICK;

    public static ClickAction valueOf(EnumWrappers.EntityUseAction action) {
        switch (action) {
            case INTERACT:
                return RIGHT_CLICK;
            case ATTACK:
                return LEFT_CLICK;
        }
        return null;
    }
}
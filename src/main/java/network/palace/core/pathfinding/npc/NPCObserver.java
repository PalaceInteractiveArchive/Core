package network.palace.core.pathfinding.npc;

import network.palace.core.player.CPlayer;

/**
 * @author Innectic
 * @since 1/21/2017
 */
public interface NPCObserver {
    void onPlayerInteract(CPlayer player, AbstractMob mob, ClickAction action);
}

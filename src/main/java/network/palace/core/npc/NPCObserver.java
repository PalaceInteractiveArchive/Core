package network.palace.core.npc;

import network.palace.core.player.CPlayer;

public interface NPCObserver {
    void onPlayerInteract(CPlayer player, AbstractMob mob, ClickAction action);
}

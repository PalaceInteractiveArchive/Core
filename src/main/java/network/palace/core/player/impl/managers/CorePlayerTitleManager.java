package network.palace.core.player.impl.managers;

import lombok.AllArgsConstructor;
import network.palace.core.player.CPlayer;
import network.palace.core.player.CPlayerTitleManager;
import network.palace.core.player.PlayerStatus;

/**
 * The type Core player title manager.
 */
@AllArgsConstructor
public class CorePlayerTitleManager implements CPlayerTitleManager {

    private final CPlayer player;

    @Override
    public void show(String title) {
        show(title, "");
    }

    @Override
    public void show(String title, String subtitle) {
        show(title, subtitle, 1, 7, 2);
    }

    @Override
    public void show(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        if (player.getStatus() != PlayerStatus.JOINED) return;
        if (player.getBukkitPlayer() == null) return;
        player.getBukkitPlayer().sendTitle(title, subtitle, fadeIn * 20, stay * 20, fadeOut * 20);
    }

    @Override
    public void hide() {
        if (player.getStatus() != PlayerStatus.JOINED) return;
        if (player.getBukkitPlayer() == null) return;
        player.getBukkitPlayer().resetTitle();
    }
}

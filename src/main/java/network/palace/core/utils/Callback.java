package network.palace.core.utils;

import network.palace.core.player.CPlayer;

/**
 * @author Innectic
 * @since 8/5/2017
 */
public class Callback {
    public interface Finished {
        void finished();
    }
    public interface PlayerCallback extends Finished {
        void action(CPlayer player);
    }
}

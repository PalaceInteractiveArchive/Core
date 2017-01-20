package network.palace.core.inventory;

import network.palace.core.player.CPlayer;

import java.util.List;

public interface CInventoryInterface {

    void open(CPlayer player);

    void close(CPlayer player);

    void open(Iterable<CPlayer> players);

    void close(Iterable<CPlayer> players);

    List<CPlayer> getCurrentObservers();

}

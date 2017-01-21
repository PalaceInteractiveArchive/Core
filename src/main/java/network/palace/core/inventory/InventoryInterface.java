package network.palace.core.inventory;

import network.palace.core.player.CPlayer;

import java.util.List;

public interface InventoryInterface {

    void open(CPlayer player);
    void close(CPlayer player);

    void open(Iterable<CPlayer> players);
    void close(Iterable<CPlayer> players);

    void addButton(InventoryButtonInterface button);
    void addButton(InventoryButtonInterface button, int slot);
    void moveButton(InventoryButtonInterface button, int slot);
    void removeButton(InventoryButtonInterface button);
    void replaceButton(InventoryButtonInterface button, int slot);

    int getSlotFor(InventoryButtonInterface button);

    void clearSlot(int slot);
    boolean isFilled(int slot);

    int getSize();
    List<CPlayer> getCurrentObservers();
    List<InventoryButtonInterface> getButtons();

}

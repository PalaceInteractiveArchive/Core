package network.palace.core.inventory.impl;

import com.google.common.collect.ImmutableList;
import network.palace.core.Core;
import network.palace.core.inventory.InventoryButtonInterface;
import network.palace.core.inventory.InventoryInterface;
import network.palace.core.player.CPlayer;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Marc on 4/3/17.
 */
public class DynamicInventory implements InventoryInterface, Listener {
    protected final List<CPlayer> observers = new LinkedList<>();
    protected final int size;
    protected final String title;
    protected final Map<Integer, InventoryButtonInterface> inventoryButtons = new HashMap<>();

    /**
     * Constructor for Dynamic Inventory
     *
     * @param size  size of inventory
     * @param title title of inventory
     *              %s = player name
     */
    public DynamicInventory(int size, String title) {
        if (size % 9 != 0) {
            throw new IllegalArgumentException("The size of an inventory must be divisible by 9");
        }
        this.size = size;
        this.title = title;
        Core.registerListener(this);
    }

    @Override
    public void open(CPlayer player) {
        if (observers.contains(player)) return;
        observers.add(player);
        String title = String.format(this.title, player.getName());
        org.bukkit.inventory.Inventory inv = Core.createInventory(size, title);
        for (Map.Entry<Integer, InventoryButtonInterface> entry : inventoryButtons.entrySet()) {
            if (!entry.getValue().isVisible(player)) {
                continue;
            }
            inv.setItem(entry.getKey(), entry.getValue().getStack());
        }
        player.openInventory(inv);
    }

    @Override
    public void close(CPlayer player) {
        if (!observers.contains(player)) return;
        observers.remove(player);
        player.getBukkitPlayer().closeInventory();
    }

    @Override
    public void open(Iterable<CPlayer> players) {
        players.forEach(this::open);
    }

    @Override
    public void close(Iterable<CPlayer> players) {
        players.forEach(this::close);
    }

    /**
     * Adds a button, will replace default to the next available slot. Will not add if there is no room remaining.
     *
     * @param button The {@link InventoryButtonInterface} to add to the inventory GUI.
     */
    @Override
    public final void addButton(InventoryButtonInterface button) {
        int nextOpenSlot = getNextOpenSlot();
        if (nextOpenSlot == -1) return;
        addButton(button, nextOpenSlot);
    }

    /**
     * Adds a button to the GUI at a specific location and will overwrite the current button at that location.
     *
     * @param button The {@link InventoryButtonInterface} to add to the inventory GUI.
     * @param slot   The slot to place that button at.
     */
    @Override
    public final void addButton(InventoryButtonInterface button, int slot) {
        inventoryButtons.put(slot, button);
    }

    @Override
    public final void moveButton(InventoryButtonInterface button, int slot) {
        removeButton(button);
        addButton(button, slot);
    }

    @Override
    public final void removeButton(InventoryButtonInterface button) {
        clearSlot(getSlotFor(button));
    }

    @Override
    public final void replaceButton(InventoryButtonInterface button, int slot) {
        clearSlot(slot);
        addButton(button, slot);
    }

    @Override
    public void clearSlot(int slot) {
        if (!inventoryButtons.containsKey(slot)) return;
        inventoryButtons.remove(slot);
    }

    @Override
    public final int getSlotFor(InventoryButtonInterface button) {
        for (Map.Entry<Integer, InventoryButtonInterface> integerInventoryButtonEntry : inventoryButtons.entrySet()) {
            if (integerInventoryButtonEntry.getValue().equals(button)) {
                return integerInventoryButtonEntry.getKey();
            }
        }
        return -1;
    }

    @Override
    public boolean isFilled(int slot) {
        return inventoryButtons.containsKey(slot);
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public final ImmutableList<CPlayer> getCurrentObservers() {
        return ImmutableList.copyOf(observers);
    }

    @Override
    public final ImmutableList<InventoryButtonInterface> getButtons() {
        return ImmutableList.copyOf(inventoryButtons.values());
    }

    private int getNextOpenSlot() {
        int nextSlot = 0;
        for (int integer : inventoryButtons.keySet()) {
            if (integer == nextSlot) nextSlot = integer + 1;
        }
        return nextSlot >= inventoryButtons.size() ? -1 : nextSlot;
    }
}

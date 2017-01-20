package network.palace.core.inventory;

import com.google.common.collect.ImmutableList;
import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;

import java.util.*;

public class CoreInventory implements CInventoryInterface, Listener {

    protected final List<CPlayer> observers = new LinkedList<>();
    protected final String title;
    protected Inventory inventory;
    protected final Map<Integer, InventoryButton> inventoryButtons = new HashMap<>();
    protected Set<Integer> updatedSlots = new HashSet<>();

    public CoreInventory(int size, String title) {
        if (size % 9 != 0) {
            throw new IllegalArgumentException("The size of an inventory must be divisible by 9");
        }
        this.title = title;
        this.inventory = Core.createInventory(size, title);
        Core.registerListener(this);
    }

    @Override
    public final void open(CPlayer player) {
        if (observers.contains(player)) return;
        observers.add(player);
        player.getBukkitPlayer().openInventory(inventory);
        onOpen(player);
    }

    @Override
    public final void close(CPlayer player) {
        if (!observers.contains(player)) return;
        observers.remove(player);
        player.getBukkitPlayer().closeInventory();
        onClose(player);
    }

    @Override
    public final void open(Iterable<CPlayer> players) {
        players.forEach(this::open);
    }

    @Override
    public final void close(Iterable<CPlayer> players) {
        players.forEach(this::close);
    }

    @Override
    public final ImmutableList<CPlayer> getCurrentObservers() {
        return ImmutableList.copyOf(observers);
    }

    /**
     * Adds a button, will replace default to the next available slot. Will not add if there is no room remaining.
     *
     * @param button The {@link network.palace.core.inventory.InventoryButton} to add to the inventory GUI.
     */
    public final void addButton(InventoryButton button) {
        int nextOpenSlot = getNextOpenSlot();
        if (nextOpenSlot == -1) return;
        addButton(button, nextOpenSlot);
    }

    /**
     * Adds a button to the GUI at a specific location and will overwrite the current button at that location.
     *
     * @param button The {@link network.palace.core.inventory.InventoryButton} to add to the inventory GUI.
     * @param slot   The slot to place that button at.
     */
    public final void addButton(InventoryButton button, int slot) {
        inventoryButtons.put(slot, button);
        markForUpdate(slot);
    }

    public final void moveButton(InventoryButton button, int slot) {
        removeButton(button);
        addButton(button, slot);
    }

    private void markForUpdate(int slot) {
        updatedSlots.add(slot);
        updateInventory();
    }

    public final int getSlotFor(InventoryButton button) {
        for (Map.Entry<Integer, InventoryButton> integerInventoryButtonEntry : inventoryButtons.entrySet()) {
            if (integerInventoryButtonEntry.getValue().equals(button)) {
                return integerInventoryButtonEntry.getKey();
            }
        }
        return -1;
    }

    public final void removeButton(InventoryButton button) {
        clearSlot(getSlotFor(button));
    }

    public void clearSlot(int slot) {
        if (!inventoryButtons.containsKey(slot)) return;
        inventoryButtons.remove(slot);
        markForUpdate(slot);
    }

    public int getSize() {
        return inventory.getSize();
    }

    public void onOpen(CPlayer player) {
    }

    public void onClose(CPlayer player) {
    }

    public final boolean isFilled(int slot) {
        return inventoryButtons.containsKey(slot);
    }

    public final void updateInventory() {
        for (int i = 0; i < inventory.getSize(); i++) {
            InventoryButton inventoryButton = inventoryButtons.get(i);
            if (inventoryButton == null && inventory.getItem(i) != null) {
                inventory.setItem(i, null);
                continue;
            }
            if ((inventory.getItem(i) == null && inventoryButton != null) || updatedSlots.contains(i)) {
                inventory.setItem(i, inventoryButton.getStack());
            }
        }
        observers.forEach(observer -> observer.getBukkitPlayer().updateInventory());
        updatedSlots = new HashSet<>();
    }

    private int getNextOpenSlot() {
        int nextSlot = 0;
        for (int integer : inventoryButtons.keySet()) {
            if (integer == nextSlot) nextSlot = integer + 1;
        }
        return nextSlot >= inventory.getSize() ? -1 : nextSlot;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public final void onPlayerLeave(PlayerQuitEvent event) {
        CPlayer onlinePlayer = Core.getPlayerManager().getPlayer(event.getPlayer());
        if (observers.contains(onlinePlayer)) observers.remove(onlinePlayer);
    }

    @EventHandler
    public final void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;
        if (!event.getInventory().equals(inventory)) return;
        Player player = (Player) event.getPlayer();
        CPlayer onlinePlayer = Core.getPlayerManager().getPlayer(player);
        observers.remove(onlinePlayer);
        onClose(onlinePlayer);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public final void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        if (!(event.getInventory().equals(inventory))) return;
        CPlayer player = Core.getPlayerManager().getPlayer((Player) event.getWhoClicked());
        InventoryButton inventoryButton = inventoryButtons.get(event.getSlot());
        if (player == null) return;
        if (inventoryButton == null) return;
        inventoryButton.getClick().onPlayerClick(player, ClickAction.getActionTypeFor(event.getClick()));
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public final void onPlayerInventoryMove(InventoryMoveItemEvent event) {
        if (!event.getDestination().equals(inventory)) return;
        event.setCancelled(true);
    }

    public final ImmutableList<InventoryButton> getButtons() {
        return ImmutableList.copyOf(inventoryButtons.values());
    }
}

package network.palace.core.menu;

import lombok.Getter;
import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Menu implements Listener {
    private static final List<Menu> toOpen = new ArrayList<>();

    static {
        Core.runTaskTimer(Core.getInstance(), () -> {
            List<Menu> opened = new ArrayList<>();
            toOpen.stream().filter(Objects::nonNull).forEach((menu) -> {
                menu.open_internal();
                opened.add(menu);
            });
            toOpen.removeAll(opened);
        }, 0L, 5L);
    }

    @Getter private final int size;
    private final List<MenuButton> menuButtons;
    private final Inventory inventory;
    private final String title;
    private final CPlayer player;
    private Runnable onClose = null;

    private final Runnable callback;
    private boolean hasCallbackRun = false;

    public Menu(int size, String title, CPlayer player, List<MenuButton> buttons) {
        this(size, title, player, buttons, null);
    }

    public Menu(int size, String title, CPlayer player, List<MenuButton> buttons, Runnable callback) {
        this.size = size;
        this.inventory = Core.createInventory(size, title);
        this.title = title;
        this.player = player;
        this.menuButtons = buttons;
        this.callback = callback;
    }

    public Optional<MenuButton> getButton(int slot) {
        return menuButtons.stream().filter(b -> b.getSlot() == slot).findFirst();
    }

    public void open() {
        if (!hasCallbackRun) {
            hasCallbackRun = true;
            if (this.callback != null) {
                Core.runTaskAsynchronously(Core.getInstance(), () -> {
                    callback.run();
                    toOpen.add(this);
                });
                return;
            }
            toOpen.add(this);
        }
    }

    private void open_internal() {
        inventory.clear();
        menuButtons.forEach(button -> {
            if (button.getSlot() >= inventory.getSize()) {
                throw new IllegalArgumentException("Button " + button.getItemStack().toString() + " in slot " +
                        button.getSlot() + " exceeds inventory " + title + " size " + inventory.getSize());
            }
            inventory.setItem(button.getSlot(), button.getItemStack());
        });
        player.openInventory(inventory);
        Core.registerListener(this);
    }

    public void removeButton(int slot) {
        menuButtons.removeIf(b -> b.getSlot() == slot);
        inventory.setItem(22, null);
    }

    public void setButton(MenuButton button) {
        menuButtons.removeIf(b -> b.getSlot() == button.getSlot());
        menuButtons.add(button);
        inventory.setItem(button.getSlot(), button.getItemStack());
    }

    @EventHandler
    public void click(InventoryClickEvent event) {
        long t = System.currentTimeMillis();
        InventoryView view = event.getView();
        if (isSameInventory(view)) {
            event.setCancelled(true);
            menuButtons.stream().filter(button -> button.getSlot() == event.getRawSlot() && button.getActions().containsKey(event.getClick()))
                    .findFirst().map(menuButton -> menuButton.getActions().get(event.getClick()))
                    .ifPresent(action -> action.accept(Core.getPlayerManager().getPlayer((Player) event.getWhoClicked())));

            long t2 = System.currentTimeMillis();
            long diff = t2 - t;
            if (diff >= 500) {
                for (CPlayer cp : Core.getPlayerManager().getOnlinePlayers()) {
                    if (cp == null)
                        continue;
                    if (cp.getRank().getRankId() >= Rank.DEVELOPER.getRankId()) {
                        cp.sendMessage(ChatColor.RED + "Click event took " + diff + "ms! " + ChatColor.GREEN +
                                event.getWhoClicked().getName() + " " + ChatColor.stripColor(view.getTitle()) + " ");
                    }
                }
            }
        }
    }

    public void setOnClose(Runnable onClose) {
        this.onClose = onClose;
    }

    @EventHandler
    public void close(InventoryCloseEvent event) {
        if (isSameInventory(event.getView())) {
            HandlerList.unregisterAll(this);
            if (onClose != null) {
                onClose.run();
            }
        }
    }

    private boolean isSameInventory(InventoryView view) {
        if (view == null) {
            return false;
        }

        if (this.inventory == null) {
            return false;
        }

        return view.getTitle().equals(title) && view.getTopInventory().getViewers().stream().anyMatch(p -> p.getUniqueId().equals(player.getUniqueId()));
    }
}

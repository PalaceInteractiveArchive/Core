package network.palace.core.economy;

import com.google.common.collect.ImmutableMap;
import network.palace.core.menu.Menu;
import network.palace.core.menu.MenuButton;
import network.palace.core.player.CPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BankMenu {
    private CPlayer player;

    public BankMenu(CPlayer player) {
        this.player = player;
    }

    public void openMenu() {
        List<MenuButton> buttons = new ArrayList<>();
        ItemStack balanceButton = new ItemStack(Material.CLAY_BRICK, 1);
        ItemMeta balanceMeta = balanceButton.getItemMeta();
        balanceMeta.setDisplayName(ChatColor.GREEN + "View your balances");
        buttons.add(new MenuButton(12, balanceButton, ImmutableMap.of(ClickType.LEFT, p -> {
            p.closeInventory();
            openBalance(p);
        })));
        Menu inv = new Menu(27, "Bank Menu", player, buttons);
        inv.open();
    }

    private void openBalance(CPlayer p) {
        List<MenuButton> buttons = new ArrayList<>();
        ItemStack adventureCoins = new ItemStack(Material.BOWL, p.getAdventureCoins());
        ItemMeta advMeta = adventureCoins.getItemMeta();
        advMeta.setDisplayName(ChatColor.GREEN + "Adventure Coins");
        advMeta.setLore(Collections.singletonList(ChatColor.LIGHT_PURPLE + "Adventure Coins are the primary currency of the theme parks. They are earned by riding rides, and watching shows."));
        buttons.add(new MenuButton(12, adventureCoins));

        Menu inv = new Menu(27, "Bank Balances", p, buttons);
        inv.open();
    }

}

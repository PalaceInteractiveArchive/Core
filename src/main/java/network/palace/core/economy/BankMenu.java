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
import java.util.List;

public class BankMenu {
    private final CPlayer player;

    public BankMenu(CPlayer player) {
        this.player = player;
    }

    public void openMenu() {
        List<MenuButton> buttons = new ArrayList<>();
        ItemStack balanceButton = new ItemStack(Material.CLAY_BRICK);
        ItemMeta balanceMeta = balanceButton.getItemMeta();
        balanceMeta.setDisplayName(ChatColor.GREEN + "View your balances");
        balanceButton.setItemMeta(balanceMeta);
        buttons.add(new MenuButton(12, balanceButton, ImmutableMap.of(ClickType.LEFT, p -> {
            p.closeInventory();
            openBalance(p);
        })));

        ItemStack utilMenu = new ItemStack(Material.GOLD_RECORD);
        ItemMeta utilMeta = utilMenu.getItemMeta();
        utilMeta.setDisplayName(ChatColor.GREEN + "Banking Utilities");
        List<String> utilLore = new ArrayList<>();
        utilLore.add(ChatColor.LIGHT_PURPLE + "In this menu you can manage all aspects of");
        utilLore.add(ChatColor.LIGHT_PURPLE + "your bank account, such as:");
        utilLore.add(ChatColor.GOLD + "• Trading");
        utilLore.add(ChatColor.GOLD + "• Recent Spending");
        utilLore.add(ChatColor.GOLD + "• Leaderboards");
        utilMeta.setLore(utilLore);
        utilMenu.setItemMeta(utilMeta);
        buttons.add(new MenuButton(14, utilMenu, ImmutableMap.of(ClickType.LEFT, p -> {
            p.closeInventory();
            openUtilsMenu();
        })));

        Menu inv = new Menu(27, "Bank Menu", player, buttons);
        inv.open();
    }

    private void openBalance(CPlayer p) {
        List<MenuButton> buttons = new ArrayList<>();
        ItemStack adventureCoins = new ItemStack(Material.GOLD_INGOT);
        ItemMeta advMeta = adventureCoins.getItemMeta();
        advMeta.setDisplayName(ChatColor.GREEN + "Adventure Coins");
        List<String> advLore = new ArrayList<>();
        advLore.add(ChatColor.AQUA + "You currently have:");
        advLore.add(p.getAdventureCoins() + " Adventure Coins!");
        advLore.add(ChatColor.LIGHT_PURPLE + "Adventure Coins are the primary currency of the theme parks.");
        advLore.add(ChatColor.LIGHT_PURPLE + "They are earned by riding rides, and watching shows.");
        advMeta.setLore(advLore);
        adventureCoins.setItemMeta(advMeta);
        buttons.add(new MenuButton(12, adventureCoins));

        ItemStack backButton = new ItemStack(Material.STICK);
        ItemMeta backMeta = backButton.getItemMeta();
        backMeta.setDisplayName(ChatColor.RED + "Go Back");
        backButton.setItemMeta(backMeta);

        buttons.add(new MenuButton(24, backButton, ImmutableMap.of(ClickType.LEFT, user -> {
            user.closeInventory();
            openMenu();
        })));

        Menu inv = new Menu(27, "Bank Balances", p, buttons);
        inv.open();
    }

    private void openUtilsMenu() {
        List<MenuButton> buttons = new ArrayList<>();

        ItemStack tradingMenu = new ItemStack(Material.SKULL_ITEM);
        ItemMeta tradingMeta = tradingMenu.getItemMeta();
        tradingMeta.setDisplayName(ChatColor.GREEN + "Trading Menu");
        tradingMenu.setItemMeta(tradingMeta);

        ItemStack transactionsMenu = new ItemStack(Material.CLAY_BRICK);
        ItemMeta transacationsMeta = transactionsMenu.getItemMeta();
        transacationsMeta.setDisplayName(ChatColor.GREEN + "Transactions Menu");
        transactionsMenu.setItemMeta(transacationsMeta);

        ItemStack leaderboardMenu = new ItemStack(Material.ARROW);
        ItemMeta leaderboardMeta = leaderboardMenu.getItemMeta();
        leaderboardMeta.setDisplayName(ChatColor.GREEN + "Leaderboards Menu");
        leaderboardMenu.setItemMeta(leaderboardMeta);

        buttons.add(new MenuButton(11, tradingMenu, ImmutableMap.of(ClickType.LEFT, p -> {
            p.closeInventory();
            //todo
        })));

        buttons.add(new MenuButton(13, transactionsMenu, ImmutableMap.of(ClickType.LEFT, p -> {
            p.closeInventory();
            //todo
        })));

        buttons.add(new MenuButton(15, leaderboardMenu, ImmutableMap.of(ClickType.LEFT, p -> {
            p.closeInventory();
            //todo
        })));

        ItemStack backButton = new ItemStack(Material.STICK);
        ItemMeta backMeta = backButton.getItemMeta();
        backMeta.setDisplayName(ChatColor.RED + "Go Back");
        backButton.setItemMeta(backMeta);

        buttons.add(new MenuButton(24, backButton, ImmutableMap.of(ClickType.LEFT, user -> {
            user.closeInventory();
            openMenu();
        })));

        Menu inv = new Menu(27, "Bank Utilities", player, buttons);
        inv.open();
    }
}

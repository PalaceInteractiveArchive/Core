package network.palace.core.crafting;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import network.palace.core.Core;
import network.palace.core.achievements.CoreAchievement;
import network.palace.core.player.CPlayer;
import network.palace.core.utils.HeadUtil;
import network.palace.core.utils.ItemUtil;
import network.palace.core.utils.MiscUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class CraftingMenu implements Listener {
    //Pages
    private ItemStack nextPage = ItemUtil.create(Material.ARROW, ChatColor.GREEN + "Next Page");
    private ItemStack lastPage = ItemUtil.create(Material.ARROW, ChatColor.GREEN + "Last Page");
    private List<UUID> refresh = new ArrayList<>();


    public CraftingMenu() {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(Core.getInstance(),
                PacketType.Play.Server.WINDOW_ITEMS, PacketType.Play.Server.SET_SLOT, PacketType.Play.Client.SET_CREATIVE_SLOT, PacketType.Play.Client.WINDOW_CLICK) {
            @Override
            public void onPacketSending(PacketEvent event) {
                CPlayer player = Core.getPlayerManager().getPlayer(event.getPlayer());
                if (player == null || player.getGamemode().equals(GameMode.CREATIVE) || player.getGamemode().equals(GameMode.SPECTATOR))
                    return;
                PacketContainer cont = event.getPacket();
                PacketType type = cont.getType();
                if (type.equals(PacketType.Play.Server.WINDOW_ITEMS)) {
                    List<ItemStack> item = cont.getItemListModifier().read(0);
                    if (item.size() != 46) return;
                    ItemStack[] array = getMenuItems(player);
                    for (int i = 0; i < array.length; i++) {
                        ItemStack itm = array[i];
                        if (itm != null && !itm.getType().equals(Material.AIR)) {
                            item.set(i, itm);
                        }
                    }
                    cont.getItemListModifier().write(0, item);
                } else if (type.equals(PacketType.Play.Server.SET_SLOT)) {
                    StructureModifier<Integer> mod = cont.getIntegers();
                    int id = mod.read(0);
                    int slot = mod.read(1);
                    ItemStack item = cont.getItemModifier().read(0);
                    if (id != 0 || slot >= 5) return;
                    ItemStack[] array = getMenuItems(player);
                    cont.getItemModifier().write(0, array[slot]);
                }
            }

            @Override
            public void onPacketReceiving(PacketEvent event) {
                CPlayer player = Core.getPlayerManager().getPlayer(event.getPlayer());
                if (player == null)
                    return;
                boolean survival = player.getGamemode().equals(GameMode.SURVIVAL) || player.getGamemode().equals(GameMode.ADVENTURE);
                PacketContainer cont = event.getPacket();
                PacketType type = cont.getType();
                if (type.equals(PacketType.Play.Client.SET_CREATIVE_SLOT) && !survival) {
                    ItemStack item = cont.getItemModifier().read(0);
                    if (item == null || item.getItemMeta() == null || item.getItemMeta().getDisplayName() == null)
                        return;
                    String name = item.getItemMeta().getDisplayName();
                    ItemStack[] array = getMenuItems(player);
                    for (ItemStack i : array) {
                        if (i != null && i.getType().equals(item.getType()) && i.getItemMeta() != null &&
                                i.getItemMeta().getDisplayName() != null && name.equals(i.getItemMeta().getDisplayName())) {
                            event.setCancelled(true);
                            break;
                        }
                    }
                } else if (type.equals(PacketType.Play.Client.WINDOW_CLICK) && !Core.isGameMode() && survival) {
                    StructureModifier<Integer> intMod = cont.getIntegers();
                    if (intMod.read(0) != 0) return;
                    int slot = intMod.read(1);
                    switch (slot) {
                        case 1:
                        case 4:
                        case 45:
                            event.setCancelled(true);
                            player.updateInventory();
                            if (slot <= 4) update(player, slot, getMenuItems(player)[slot]);
                            break;
                        case 2:
                            event.setCancelled(true);
                            openAchievementPage(player, 1);
                            break;
                        case 3:
                            event.setCancelled(true);
                            openCosmeticsInventory(player);
                    }
                }
            }
        });
        Core.runTaskTimer(() -> Core.getPlayerManager().getOnlinePlayers().forEach(player -> {
            Optional<InventoryView> optional = player.getOpenInventory();
            if (!optional.isPresent() || player.getGamemode().equals(GameMode.CREATIVE) || player.getGamemode().equals(GameMode.SPECTATOR)) {
                refresh.remove(player.getUniqueId());
                return;
            }
            InventoryView view = optional.get();
            if (view.getType().equals(InventoryType.CRAFTING)) {
                boolean contains = refresh.remove(player.getUniqueId());
                if (!contains) return;
                player.updateInventory();
                update(player);
                return;
            }
            refresh.add(player.getUniqueId());
        }), 0L, 10L);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        CPlayer player = Core.getPlayerManager().getPlayer(event.getWhoClicked().getUniqueId());
        if (player == null) return;
        Inventory inv = event.getClickedInventory();
        if (!inv.getName().startsWith(ChatColor.BLUE + "Achievements Page ")) return;
        event.setCancelled(true);
        String name;
        try {
            name = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName());
        } catch (Exception ignored) {
            return;
        }
        int page = Integer.parseInt(ChatColor.stripColor(inv.getName()).replaceAll("Achievements Page ", ""));
        switch (name) {
            case "Next Page":
                openAchievementPage(player, page + 1);
                break;
            case "Last Page":
                openAchievementPage(player, page - 1);
                break;
            default:
                openAchievementPage(player, page);
                break;
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        CPlayer player = Core.getPlayerManager().getPlayer(event.getPlayer().getUniqueId());
        if (player == null) return;
        Inventory inv = event.getInventory();
        String name = inv.getName();
        if (name.startsWith(ChatColor.BLUE + "Achievements Page ") || name.startsWith(ChatColor.BLUE + "Cosmetics") ||
                name.startsWith(ChatColor.BLUE + "Particles") || name.startsWith(ChatColor.BLUE + "Hats") ||
                name.startsWith(ChatColor.BLUE + "Pets")) {
            update(player);
        }
    }

    public void update(CPlayer player) {
        if (player == null) return;
        ItemStack[] array = getMenuItems(player);
        for (int i = 0; i < array.length; i++) {
            update(player, i, array[i]);
        }
    }

    public void update(CPlayer player, int slot, ItemStack item) {
        if (player == null || player.getBukkitPlayer() == null) return;
        PacketContainer cont = new PacketContainer(PacketType.Play.Server.SET_SLOT);
        StructureModifier<Integer> mod = cont.getIntegers();
        mod.write(0, 0);
        mod.write(1, slot);
        cont.getItemModifier().write(0, item);
        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player.getBukkitPlayer(), cont);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public ItemStack[] getMenuItems(CPlayer player) {
        if (player == null) return new ItemStack[5];
        ItemStack air = new ItemStack(Material.AIR);
        return new ItemStack[]{air, getPlayerHead(player), getAchievement(player),
                ItemUtil.create(Material.ANVIL, ChatColor.GREEN + "Cosmetics",
                        Collections.singletonList(ChatColor.GRAY + "Open Cosmetics Menu")),
                ItemUtil.create(Material.STORAGE_MINECART, ChatColor.GREEN + "Leveling Rewards",
                        Collections.singletonList(ChatColor.GRAY + "" + ChatColor.ITALIC + "Coming soon!"))};
    }

    public ItemStack getPlayerHead(CPlayer player) {
        ItemStack head = HeadUtil.getPlayerHead(player);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "Player Info");
        meta.setLore(Arrays.asList(ChatColor.GRAY + "Rank: " + player.getRank().getFormattedName(),
                ChatColor.GRAY + "Level: " + ChatColor.YELLOW + MiscUtil.formatNumber(Core.getHonorManager().getLevel(player.getHonor()).getLevel()),
                ChatColor.GRAY + "Honor Points: " + ChatColor.YELLOW + MiscUtil.formatNumber(player.getHonor()),
                ChatColor.GRAY + "Points to Next Level: " + ChatColor.YELLOW + MiscUtil.formatNumber(Core.getHonorManager().getNextLevel(player.getHonor()).getHonor() - player.getHonor())));
        head.setItemMeta(meta);
        return head;
    }

    public ItemStack getAchievement(CPlayer player) {
        if (player == null || player.getAchievementManager() == null) return new ItemStack(Material.AIR);
        int earned = player.getAchievementManager().getAchievements().size();
        int total = Core.getAchievementManager().getAchievements().size();
        return ItemUtil.create(Material.EMERALD, ChatColor.GREEN + "Achievements", Arrays.asList(ChatColor.GREEN +
                        "You've earned " + ChatColor.YELLOW + earned + ChatColor.GREEN + " achievements!",
                ChatColor.GREEN + "There are " + ChatColor.YELLOW + total + ChatColor.GREEN + " total to earn",
                ChatColor.GRAY + "Click to view all of your achievements"));
    }

    public void openAchievementPage(CPlayer player, int page) {
        List<CoreAchievement> achievements = Core.getAchievementManager().getAchievements();
        int size = achievements.size();
        if (size < 46) {
            page = 1;
        } else if (size < (45 * (page - 1) + 1)) {
            page -= 1;
        }
        List<CoreAchievement> list = achievements.subList(page > 1 ? (45 * (page - 1)) : 0, (size - (45 * (page - 1)))
                > 45 ? (45 * page) : size);
        int localSize = list.size();
        int invSize = 54;
        if (localSize < 46) {
            invSize = 9;
            while (invSize < localSize) {
                invSize += 9;
                if (invSize >= 45) {
                    break;
                }
            }
        }
        Inventory inv = Bukkit.createInventory(player.getBukkitPlayer(), invSize, ChatColor.BLUE + "Achievements Page " + page);
        int place = 0;
        for (CoreAchievement ach : list) {
            if (place >= 45) {
                break;
            }
            if (player.hasAchievement(ach.getId())) {
                inv.setItem(place, ItemUtil.create(Material.STAINED_CLAY, 1, (byte) 5, ChatColor.GREEN +
                        ach.getDisplayName(), Collections.singletonList(ChatColor.GRAY + "" + ChatColor.ITALIC + ach.getDescription())));
            } else {
                inv.setItem(place, ItemUtil.create(Material.STAINED_CLAY, 1, (byte) 4, ChatColor.RED +
                        ach.getDisplayName(), Collections.singletonList(ChatColor.GRAY + "" + ChatColor.ITALIC + "?")));
            }
            place++;
        }
        if (page > 1) {
            inv.setItem(48, lastPage);
        }
        int maxPage = 1;
        int n = size;
        while (true) {
            if (n - 45 > 0) {
                n -= 45;
                maxPage += 1;
            } else {
                break;
            }
        }
        if (size > 45 && page < maxPage) {
            inv.setItem(50, nextPage);
        }
        player.openInventory(inv);
    }

    private void openCosmeticsInventory(CPlayer player) {
        try {
            network.palace.cosmetics.Cosmetics.getInstance().getCosmeticsInventory().open(player);
        } catch (Exception ignored) {
        }
    }
}

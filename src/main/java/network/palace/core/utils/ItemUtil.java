package network.palace.core.utils;

import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemUtil implements Listener {

    private static final String UNABLE_TO_MOVE = "unableToMove";
    private static final String UNABLE_TO_DROP = "unableToDrop";

    @SuppressWarnings("unused")
    public static ItemStack makeUnableToMove(ItemStack stack) {
        return setNBTForItemstack(stack, UNABLE_TO_MOVE);
    }

    @SuppressWarnings("unused")
    public static ItemStack makeUnableToDrop(ItemStack stack) {
        return setNBTForItemstack(stack, UNABLE_TO_DROP);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    protected void onInventoryClick(InventoryClickEvent event) {
        if (event.getCurrentItem() == null) return;
        if (hasNBT(event.getCurrentItem(), UNABLE_TO_MOVE)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    protected void onPlayerDropItem(PlayerDropItemEvent event) {
        if (event.getItemDrop() == null) return;
        if (event.getItemDrop().getItemStack() == null) return;
        if (hasNBT(event.getItemDrop().getItemStack(), UNABLE_TO_DROP)) {
            event.setCancelled(true);
        }
    }

    public static boolean hasNBT(ItemStack stack, String tag) {
        if (stack.getType() == Material.AIR) return false;
        ItemStack craftStack = stack;
        if (!MinecraftReflection.isCraftItemStack(stack)) {
            craftStack = MinecraftReflection.getBukkitItemStack(stack);
        }
        NbtCompound nbt = (NbtCompound) NbtFactory.fromItemTag(craftStack);
        return nbt.containsKey(tag) && nbt.getInteger(tag) == 1;
    }

    public static ItemStack setNBTForItemstack(ItemStack stack, String tag) {
        ItemStack craftStack = stack;
        if (!MinecraftReflection.isCraftItemStack(stack)) {
            craftStack = MinecraftReflection.getBukkitItemStack(stack);
        }
        NbtCompound nbt = (NbtCompound) NbtFactory.fromItemTag(craftStack);
        nbt.put(tag, 1);
        NbtFactory.setItemTag(craftStack, nbt);
        return craftStack;
    }

    public static ItemStack create(Material type) {
        return create(type, 1);
    }

    public static ItemStack create(Material type, int amount) {
        return create(type, amount, (byte) 0);
    }

    public static ItemStack create(Material type, int amount, byte data) {
        return new ItemStack(type, amount, data);
    }

    public static ItemStack create(Material type, String name) {
        return create(type, name, new ArrayList<>());
    }

    public static ItemStack create(Material type, String name, List<String> lore) {
        return create(type, 1, name, lore);
    }

    public static ItemStack create(Material type, int amount, String name, List<String> lore) {
        return create(type, amount, (byte) 0, name, lore);
    }

    public static ItemStack create(Material type, int amount, byte data, String name, List<String> lore) {
        ItemStack item = create(type, amount, data);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack create(String owner) {
        return create(owner, owner, new ArrayList<>());
    }

    public static ItemStack create(String owner, String displayName) {
        return create(owner, displayName, new ArrayList<>());
    }

    public static ItemStack create(String owner, String displayName, List<String> lore) {
        ItemStack item = create(Material.SKULL_ITEM, 1, (byte) 3);
        SkullMeta sm = (SkullMeta) item.getItemMeta();
        sm.setOwner(owner);
        sm.setDisplayName(displayName);
        sm.setLore(lore);
        item.setItemMeta(sm);
        return item;
    }
}

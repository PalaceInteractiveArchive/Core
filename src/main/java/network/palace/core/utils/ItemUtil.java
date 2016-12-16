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
}

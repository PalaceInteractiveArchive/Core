package network.palace.core.utils;

import com.comphenix.protocol.utility.MinecraftReflection;
import network.palace.core.player.CPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

/**
 * The type Head util.
 */
public class HeadUtil {

    /**
     * Get a player skull ItemStack with texture of player's skin
     *
     * @param player player
     * @return ItemStack player head
     */
    public static ItemStack getPlayerHead(CPlayer player) {
        return getPlayerHead(player.getTextureValue());
    }

    /**
     * Get a player skull ItemStack from a texture hash
     *
     * @param hash hash
     * @return ItemStack player head
     */
    public static ItemStack getPlayerHead(String hash) {
        return getPlayerHead(hash, "Head");
    }

    /**
     * Get a player skull ItemStack with a custom name from a texture hash
     *
     * @param hash    hash
     * @param display display
     * @return ItemStack player head
     */
    public static ItemStack getPlayerHead(String hash, String display) {
        ItemStack head = getHead(hash);
        ItemMeta meta = head.getItemMeta();
        meta.setDisplayName(display + ChatColor.RESET);
        head.setItemMeta(meta);
        return head;
    }

    /**
     * Get a player skull ItemStack from a texture hash
     *
     * @param hash hash
     * @return ItemStack player head
     */
    private static ItemStack getHead(String hash) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        item = MinecraftReflection.getBukkitItemStack(item);

        UUID uuid = UUID.randomUUID();
        if (Bukkit.getVersion().contains("1.16")) {
            int first = (int) (uuid.getMostSignificantBits() >>> 32);
            int second = (int) (uuid.getMostSignificantBits());
            int third = (int) (uuid.getLeastSignificantBits() >>> 32);
            int fourth = (int) (uuid.getLeastSignificantBits());
            return Bukkit.getUnsafe().modifyItemStack(item, "{SkullOwner:{Id:[I;" + first + "," + second + "," + third + "," + fourth + "],Properties:{textures:[{Value:\"" + hash + "\"}]}}}");
        } else {
            return Bukkit.getUnsafe().modifyItemStack(item, "{SkullOwner:{Id:\"" + uuid.toString() + "\",Properties:{textures:[{Value:\"" + hash + "\"}]}}}");
        }
    }
}

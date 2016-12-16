package com.palacemc.core.utils;

import com.palacemc.core.player.CPlayer;
import net.minecraft.server.v1_11_R1.Item;
import net.minecraft.server.v1_11_R1.MojangsonParseException;
import net.minecraft.server.v1_11_R1.MojangsonParser;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_11_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * Created by Marc on 7/1/15
 */
public class HeadUtil {

    /**
     * Get a player skull ItemStack with texture of player's skin
     * @param player
     * @return
     * @throws MojangsonParseException
     */
    public static ItemStack getPlayerHead(CPlayer player) throws MojangsonParseException {
        return getPlayerHead(player.getTextureHash());
    }

    /**
     * Get a player skull ItemStack from a texture hash
     * @param hash
     * @return
     * @throws MojangsonParseException
     */
    public static ItemStack getPlayerHead(String hash) throws MojangsonParseException {
        return getPlayerHead(hash, "Head");
    }

    /**
     * Get a player skull ItemStack with a custom name from a texture hash
     * @param hash
     * @param display
     * @return
     * @throws MojangsonParseException
     */
    public static ItemStack getPlayerHead(String hash, String display) throws MojangsonParseException {
        net.minecraft.server.v1_11_R1.ItemStack i = new net.minecraft.server.v1_11_R1.ItemStack(Item.getById(397), 1);
        i.setData(3);
        i.setTag(MojangsonParser.parse("{display:{Name:\"" + display + ChatColor.RESET + "\"},SkullOwner:{Id:\"" +
                UUID.randomUUID() + "\",Properties:{textures:[{Value:\"" + hash + "\"}]}}}"));
        return CraftItemStack.asBukkitCopy(i);
    }
}

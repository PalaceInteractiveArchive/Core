package com.palacemc.essentials;

import org.bukkit.ChatColor;

import java.util.Arrays;

public class BoilerplateUtil {

    public static String getBoilerplateText() {
        char[] plating = new char[52];
        Arrays.fill(plating, ' ');
        return ChatColor.GOLD + "" + ChatColor.STRIKETHROUGH + new String(plating);
    }
}

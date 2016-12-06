package com.palacemc.core.commands;

import java.util.Arrays;

public class BoilerplateUtil {

    public static String getBoilerplateText(String format) {
        char[] plating = new char[64];
        Arrays.fill(plating, ' ');
        return format + new String(plating);
    }
}

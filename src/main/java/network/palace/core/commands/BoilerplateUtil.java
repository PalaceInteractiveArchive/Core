package network.palace.core.commands;

import java.util.Arrays;

/**
 * The type Boilerplate util.
 */
public class BoilerplateUtil {

    /**
     * Gets boilerplate text.
     *
     * @param format the format
     * @return the boilerplate text
     */
    public static String getBoilerplateText(String format) {
        char[] plating = new char[64];
        Arrays.fill(plating, ' ');
        return format + new String(plating);
    }
}

package network.palace.core.utils;

/**
 * The type Misc util.
 */
public class MiscUtil {

    /**
     * Check if is integer.
     *
     * @param toCheck the string to check
     * @return if is integer
     */
    public static boolean checkIfInt(String toCheck) {
        try {
            Integer.parseInt(toCheck);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Capitalize first letter of a string.
     *
     * @param input the input
     * @return the string
     */
    public static String capitalizeFirstLetter(String input) {
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }
}

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
     * Check if something is a float
     *
     * @param toCheck the string to check
     * @return if it's a float
     */
    public static boolean checkIfFloat(String toCheck) {
        try {
            Float.parseFloat(toCheck);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Check if something is a double
     *
     * @param toCheck the string to check
     * @return if it's a double
     */
    public static boolean checkIfDouble(String toCheck) {
        try {
            Double.parseDouble(toCheck);
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

    public static <T> boolean contains(T[] ts, T t) {
        if (t == null || ts == null) return false;
        for (T t1 : ts) {
            if (t1 == null) continue;
            if (t1.equals(t)) return true;
        }
        return false;
    }
}

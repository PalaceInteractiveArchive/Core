package network.palace.core.utils;

import java.text.DecimalFormat;

public class TextUtil {

    /**
     * Lazy method to add an s whenever something doesn't equal 1
     * <p>
     * Example:
     * 0 dogs
     * 1 dog
     * 2 dogs
     *
     * @param count the number of things
     * @return a blank string if count equals 1, otherwise an s
     */
    public static String pluralize(int count) {
        if (count == 1) {
            return "";
        } else {
            return "s";
        }
    }

    public static String addCommas(int number) {
        return new DecimalFormat("#,###").format(number);
    }

    public static String addCommas(double number) {
        return new DecimalFormat("#,###.00").format(number);
    }
}

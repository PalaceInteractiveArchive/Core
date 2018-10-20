package network.palace.core.utils;

import org.bukkit.Location;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class MathUtil {

    public static int floor(double num) {
        int floor = (int) num;
        return floor == num ? floor : floor - (int) (Double.doubleToRawLongBits(num) >>> 63);
    }

    /**
     * Calculate the square of a number
     *
     * @param num the number
     * @return number^2
     */
    public static double square(double num) {
        return num * num;
    }

    /**
     * Round the X,Y,Z coords of a location to a certain amount of decimal places
     *
     * @param loc    the location
     * @param places the number of decimal places to round to
     * @return the location with rounded X,Y,Z coordinates
     */
    public static Location round(Location loc, int places) {
        StringBuilder pattern = new StringBuilder("#.");
        for (int i = 0; i < places; i++) {
            pattern.append("#");
        }
        DecimalFormat df = new DecimalFormat(pattern.toString());
        df.setRoundingMode(RoundingMode.CEILING);
        loc.setX(Double.parseDouble(df.format(loc.getX())));
        loc.setY(Double.parseDouble(df.format(loc.getY())));
        loc.setZ(Double.parseDouble(df.format(loc.getZ())));
        return loc;
    }
}

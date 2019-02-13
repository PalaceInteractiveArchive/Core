package network.palace.core.utils;

import org.bukkit.block.BlockFace;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;

/**
 * The type Misc util.
 */
public class MiscUtil {
    public static final HashMap<BlockFace, Float> DIRECTIONAL_YAW = new HashMap<BlockFace, Float>() {{
        put(BlockFace.NORTH, 180F);
        put(BlockFace.EAST, -90F);
        put(BlockFace.SOUTH, 0F);
        put(BlockFace.WEST, 90F);
    }};

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

    /**
     * Add commas to a number to format it properly
     *
     * @param i the number to format
     * @return the number with commas
     */
    public static String formatNumber(int i) {
        return NumberFormat.getNumberInstance(Locale.US).format(i);
    }

    public static <T> boolean contains(T[] ts, T t) {
        if (t == null || ts == null) return false;
        for (T t1 : ts) {
            if (t1 == null) continue;
            if (t1.equals(t)) return true;
        }
        return false;
    }

    /**
     * Parse the contents of a URL and return JSON objects
     *
     * @param url the url
     * @return JSON objects with the content of the URL
     */
    public static JSONObject readJsonFromUrl(String url) {
        try (InputStream is = new URL(url).openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONParser parser = new JSONParser();
            return (JSONObject) parser.parse(jsonText);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Read all data from a buffer and output a string
     *
     * @param rd the reader
     * @return a string
     * @throws IOException in case of an error while reading the buffer
     */
    public static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }
}

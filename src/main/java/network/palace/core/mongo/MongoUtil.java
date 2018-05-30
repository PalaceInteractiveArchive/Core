package network.palace.core.mongo;

public class MongoUtil {

    public static String periodToComma(String s) {
        return s.replaceAll("\\.", ",");
    }

    public static String commaToPeriod(String s) {
        return s.replaceAll(",", ".");
    }

//    public static String sterilizePeriod(String s) {
//        return s.replaceAll("/\\./", "\\u002e");
//    }
}

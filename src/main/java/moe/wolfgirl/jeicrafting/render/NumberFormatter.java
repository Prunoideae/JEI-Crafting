package moe.wolfgirl.jeicrafting.render;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class NumberFormatter {
    private static final NavigableMap<Long, String> SUFFIXES = new TreeMap<>();

    static {
        SUFFIXES.put(1_000L, "K");
        SUFFIXES.put(1_000_000L, "M");
        SUFFIXES.put(1_000_000_000L, "B");
        SUFFIXES.put(1_000_000_000_000L, "T");
        // Add more if needed: P for Peta, E for Exa (would require 'long' input for method)
    }

    private static final DecimalFormat ONE_DECIMAL_FORMAT = new DecimalFormat("0.#");

    public static String formatInt(int number) {
        if (number == 0) return "0";

        long num = number;
        String sign = "";
        if (num < 0) {
            sign = "-";
            num = -num;
        }

        if (num < 1000) return sign + num;

        Map.Entry<Long, String> entry = SUFFIXES.floorEntry(num);

        if (entry == null) {
            return sign + num;
        }

        Long divisor = entry.getKey();
        String suffix = entry.getValue();

        double valueToFormat = (double) num / divisor;

        return sign + ONE_DECIMAL_FORMAT.format(valueToFormat) + suffix;
    }
}

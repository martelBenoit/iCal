package ical.util;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author Yakovenko Denis
 * @version 0.0.1
 *
 *
 * Provides one static method for getting a hex representation of a color,
 * and a helper method for checking if the color of the specified name is
 * actually available in the class.
 *  <p>
 * Usage:
 *      1. {@code Colorize.get()} - returns a random hex color
 *      2. {@code Colorize.get(String)} - returns a hex color of a spacified name.
 *         If the color doesn't exist in the class - returns {@code null}.
 *      3. {@code Colorize.checkColorAvailability(String)} - returns a {@code boolean} value that
 *         represents color's availability: <b>true</b> - if available, <b>false</b> - if not.
 * </p>
 */
public class Colorize {

    /**
     * Contains all the colors of the class (with the names of each color).
     */
    private static final Map<String, String> colorsMap = new LinkedHashMap<String, String>() {
        {

            put("#c2185b","#c2185b");
            put("#7b1fa2","#7b1fa2");
            put("#512da8","#512da8");
            put("#303f9f","#303f9f");
            put("#1976d2","#1976d2");
            put("#0288d1","#0288d1");
            put("#0097a7","#0097a7");
            put("#00796b","#00796b");
            put("#388e3c","#388e3c");
            put("#afb42b","#afb42b");
            put("#fbc02d","#fbc02d");
            put("#ffa000","#ffa000");
            put("#f57c00","#f57c00");
            put("#e64a19","#e64a19");
            put("#616161","#616161");
            put("#455a64","#455a64");

        }
    };

    /**
     * Contains all the colors of the class (without their names, just the hex values).
     */
    private static final Object[] colorsArray = colorsMap.values().toArray();



    /**
     * Returns a random hex color string.
     *
     * @param seed the seed
     * @return Random hex <b>String</b> color representation.
     */
    public static String get(long seed) {
        Random generator = new Random();
        generator.setSeed(seed);
        return (String) colorsArray[generator.nextInt(colorsArray.length)];
    }


    public static String get(long seed, int multiple){
        int i = 0;

        String res = null;
        Random generator = new Random();
        generator.setSeed(seed);

        if(multiple <= 0)
            return get(seed);

        while(i < multiple){
            res = (String) colorsArray[generator.nextInt(colorsArray.length)];
            i++;
        }

        return res;
    }

    /**
     * Returns a hex color string
     *
     * @param colorName the name of the needed color.
     * @return Hex <b>String</b> color representation if it exists, and {@code null} if the color doesn't exist in the
     * class.
     */
    public static String get(String colorName) {
        return colorsMap.get(colorName);
    }

}
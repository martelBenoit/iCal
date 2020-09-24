package ical.util;

import java.awt.*;
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
 *         represents color's availability: <tt>true</tt> - if available, <tt>false</tt> - if not.
 * </p>
 */
public class Colorize {

    /**
     * Contains all the colors of the class (with the names of each color).
     */
    private static final Map<String, String> colorsMap = new LinkedHashMap<String, String>() {
        {

            put("dbd3d8","#dbd3d8");
            put("82c0cc","#82c0cc");
            put("368f8b","#368f8b");
            put("70c1b3","#70c1b3");
            put("a6808c","#a6808c");
            put("73a942","#73a942");
            put("da627d","#da627d");
            put("c17c74","#c17c74");
            put("bcac9b","#bcac9b");
            put("e8998d","#e8998d");
            put("a1c181","#a1c181");
            put("ffc09f","#ffc09f");
            put("9db4c0","#9db4c0");
            put("ccb7ae","#ccb7ae");
            put("577399","#577399");
            put("d4bebe","#d4bebe");
            put("b7b7a4","#b7b7a4");
            put("d4e09b","#d4e09b");
            put("c18c5d","#c18c5d");
            put("ce796b","#ce796b");
            put("b0c4b1","#b0c4b1");
            put("cfe0c3","#cfe0c3");

        }
    };

    /**
     * Contains all the colors of the class (without their names, just the hex values).
     */
    private static final Object[] colorsArray = colorsMap.values().toArray();



    /**
     * Returns a random hex color string.
     *
     * @return Random hex <tt>String</tt> color representation.
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
     * @return Hex <tt>String</tt> color representation if it exists, and {@code null} if the color doesn't exist in the
     * class.
     */
    public static String get(String colorName) {
        return colorsMap.get(colorName);
    }

}
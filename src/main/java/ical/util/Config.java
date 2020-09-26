package ical.util;

import io.github.cdimascio.dotenv.Dotenv;

/**
 * Config class.
 *
 * <br>Load the config file.
 *
 * @author Benoît Martel
 * @version 1.0
 * @see <a href="https://github.com/cdimascio/java-dotenv">The complete dotenv documentation</a>
 */
public class Config {

    /**
     * Load the file .env
     */
    private static final Dotenv dotenv = Dotenv.load();

    /**
     * Get the value of the key passed in parameter.
     *
     * @param key the key
     * @return the value of the key passed in parameter
     */
    public static String get(String key){
        return dotenv.get(key.toUpperCase());
    }

}
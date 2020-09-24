package ical.database;

import ical.util.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * ConnectionSingleton class.
 *
 * <br>Used to create the connection to the database and provides an instance of this connection once established.
 *
 * @author Benoît Martel
 * @version 1.0
 */
public class ConnectionSingleton {

    /**
     * the logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionSingleton.class);

    /**
     * the connection.
     */
    private static Connection connection;

    /**
     * Get the instance of the connection of the database.
     *
     * @return the instance of the connection of the database
     */
    public static Connection getInstance(){
        if(connection == null){

            try{
                connection = DriverManager.getConnection(
                        Config.get("DB_URL"),
                        Config.get("DB_USERNAME"),
                        Config.get(("DB_PASSWORD"))
                );
                LOGGER.info("Connected to PostgreSQL database!");
            } catch (SQLException e){
                LOGGER.info("Connection failure.");
                e.printStackTrace();
            }
        }
            
        return connection;
    }

}

package ical.database;

import ical.util.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionSingleton {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionSingleton.class);

    private static Connection connection;

    public static Connection getInstance(){
        if(connection == null){

            try{
                connection = DriverManager.getConnection(Config.get("DB_URL"),Config.get("DB_USERNAME"),Config.get(("DB_PASSWORD")));
                LOGGER.info("Connected to PostgreSQL database!");
            } catch (SQLException e){
                LOGGER.info("Connection failure.");
                e.printStackTrace();
            }
        }
            
        return connection;
    }

}
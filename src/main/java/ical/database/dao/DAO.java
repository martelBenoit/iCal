package ical.database.dao;

import ical.database.entity.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.Connection;
import java.util.ArrayList;


/**
 * DAO abstract class.
 *
 * <br> This class is used as a template to create the DAOs for the entities present in the database.
* @param <T> the entity object used for the DAO
 */

public abstract class DAO<T extends Entity> {

    /**
     * the sql connection.
     */
    protected Connection conn;

    /**
     * the logger.
     */
    protected static final Logger LOGGER = LoggerFactory.getLogger(DAO.class);

    /**
     * Constructor.
     *
     * @param conn the sql connection
     */
    public DAO(@Nonnull Connection conn){
        this.conn = conn;
    }

    @Nullable
    public abstract T create(@Nonnull T obj);

    public abstract boolean delete(@Nonnull T obj);

    public abstract boolean update(@Nonnull T obj);

    @Nonnull
    public abstract ArrayList<T> findAll();


}

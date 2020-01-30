package ical.database.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.Connection;
import java.util.ArrayList;

public abstract class DAO<T> {

    protected Connection conn;

    protected static final Logger LOGGER = LoggerFactory.getLogger(DAO.class);

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

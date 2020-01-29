package ical.database.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.ArrayList;

public abstract class DAO<T> {

    protected Connection conn;

    protected static final Logger LOGGER = LoggerFactory.getLogger(DAO.class);

    public DAO(Connection conn){
        this.conn = conn;
    }

    public abstract T create(T obj);

    public abstract boolean delete(T obj);

    public abstract boolean update(T obj);

    public abstract ArrayList<T> findAll();


}

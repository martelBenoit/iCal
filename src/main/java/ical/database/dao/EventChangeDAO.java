package ical.database.dao;

import ical.database.entity.OEventChange;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

public class EventChangeDAO extends DAO<OEventChange> {

    public EventChangeDAO(Connection conn) {
        super(conn);
    }

    @Nullable
    @Override
    public OEventChange create(@Nonnull OEventChange obj) {

        OEventChange eventChange = null;
        try{
            String query = "INSERT INTO eventchange(id, date, type) VALUES (?,?,?)";
            PreparedStatement ps = this.conn.prepareStatement(query);
            ps.setString(1, obj.getId());
            ps.setTimestamp(2, new Timestamp(obj.getDate().getTime()));
            ps.setInt(3, obj.getType().getValue());

            if(ps.executeUpdate()==1)
                eventChange = obj;


        } catch (SQLException e){
            e.printStackTrace();
        }

        return eventChange;
    }

    @Override
    public boolean delete(@Nonnull OEventChange obj) {
        return false;
    }

    @Override
    public boolean update(@Nonnull OEventChange obj) {
        return false;
    }

    @Nonnull
    @Override
    public ArrayList<OEventChange> findAll() {
        return new ArrayList<>();
    }
}

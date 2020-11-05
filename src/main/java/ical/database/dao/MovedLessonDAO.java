package ical.database.dao;

import ical.database.entity.MovedLesson;
import ical.util.ModificationType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.*;
import java.util.ArrayList;

public class MovedLessonDAO extends DAO<MovedLesson> {

    public MovedLessonDAO(Connection conn) {
        super(conn);
    }

    @Nullable
    @Override
    public MovedLesson create(@Nonnull MovedLesson obj) {

        MovedLesson movedLesson = null;
        ModificationType type = null;

        String query;
        PreparedStatement ps = null;

        if(obj.getPreviousLesson() != null){
            if(obj.getActualLesson() != null){
                type = ModificationType.MOVE;
            }
            else
                type = ModificationType.REMOVE;
        }
        else if(obj.getActualLesson() != null){
            type = ModificationType.ADD;
        }

        try{
            if(type == ModificationType.ADD){
                query = "INSERT INTO movedlesson(actuallesson) VALUES (?)";
                ps = this.conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1,obj.getActualLesson().getUniqueID());
            }
            else if(type == ModificationType.REMOVE){
                query = "INSERT INTO movedlesson(previouslesson) VALUES (?)";
                ps = this.conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1,obj.getPreviousLesson().getUniqueID());
            }
            else if(type == ModificationType.MOVE){
                query = "INSERT INTO movedlesson(previouslesson, actuallesson) VALUES (?,?)";
                ps = this.conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1,obj.getPreviousLesson().getUniqueID());
                ps.setString(2,obj.getActualLesson().getUniqueID());
            }


            if(ps != null){
                int affectedRows = ps.executeUpdate();

                if(affectedRows == 0)
                    throw new SQLException("Creating moved lesson failed, no rows affected.");

                try(ResultSet generatedKeys = ps.getGeneratedKeys()){
                    if(generatedKeys.next()){
                        obj.setId(generatedKeys.getInt(1));
                        movedLesson = obj;
                    }
                    else {
                        throw new SQLException("Creating moved lesson failed, no ID obtained.");
                    }
                }
            }


        } catch (SQLException e){
            LOGGER.error(e.getMessage());
        }
        return movedLesson;
    }

    @Override
    public boolean delete(@Nonnull MovedLesson obj) {
        return false;
    }

    @Override
    public boolean update(@Nonnull MovedLesson obj) {
        return false;
    }

    @Nonnull
    @Override
    public ArrayList<MovedLesson> findAll() {
        return new ArrayList<>();
    }
}

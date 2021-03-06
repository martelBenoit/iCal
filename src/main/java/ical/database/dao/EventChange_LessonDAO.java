package ical.database.dao;

import ical.database.entity.EventChange_Lesson;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.*;
import java.util.ArrayList;

public class EventChange_LessonDAO extends DAO<EventChange_Lesson> {

    public EventChange_LessonDAO(Connection conn) {
        super(conn);
    }

    @Nullable
    @Override
    public EventChange_Lesson create(@Nonnull EventChange_Lesson obj) {

        EventChange_Lesson eventChangeLesson = null;

        try{
            String query = "INSERT INTO eventchange_lesson(id_event, id_moved_lesson) VALUES (?,?)";
            PreparedStatement ps = this.conn.prepareStatement(query);
            ps.setString(1,obj.getEventChange().getId());
            ps.setInt(2,obj.getMovedLesson().getId());

            int affectedRows = ps.executeUpdate();

            if(affectedRows == 0)
                throw new SQLException("Creating moved lesson failed, no rows affected.");

           eventChangeLesson = obj;

        } catch (SQLException e){
            LOGGER.error(e.getMessage());
        }
        return eventChangeLesson;
    }

    @Override
    public boolean delete(@Nonnull EventChange_Lesson obj) {
        return false;
    }

    @Override
    public boolean update(@Nonnull EventChange_Lesson obj) {
        return false;
    }

    @Nonnull
    @Override
    public ArrayList<EventChange_Lesson> findAll() {
        return new ArrayList<>();
    }
}

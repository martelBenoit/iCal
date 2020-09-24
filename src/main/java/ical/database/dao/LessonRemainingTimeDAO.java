package ical.database.dao;

import ical.database.DAOFactory;
import ical.database.entity.Lesson;
import ical.database.entity.LessonRemainingTime;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;

public class LessonRemainingTimeDAO extends DAO<LessonRemainingTime>{

    private static final Logger LOGGER = LoggerFactory.getLogger(LessonRemainingTimeDAO.class);

    public LessonRemainingTimeDAO(@NotNull Connection conn) {
        super(conn);
    }

    @Nullable
    @Override
    public LessonRemainingTime create(@NotNull LessonRemainingTime obj) {

        LessonRemainingTime res = null;

        if(!checkObject(obj))
            return null;

        LessonDAO lessonDAO = (LessonDAO) DAOFactory.getLessonDAO();
        Lesson lesson = lessonDAO.create(obj.getLesson());
        if(lesson != null){

            String lesson_id = lesson.getUniqueID();

            try{
                String query = "INSERT INTO lesson_remaining_time(id_message, id_lesson, id_channel) VALUES (?,?,?)";
                PreparedStatement ps = this.conn.prepareStatement(query);
                ps.setLong(1, obj.getId_message());
                ps.setString(2, lesson_id);
                ps.setLong(3, obj.getId_channel());


                int insert = ps.executeUpdate();
                if(insert == 1){
                    res = obj;
                }


            } catch (SQLException e){
                LOGGER.error(e.getSQLState());
                LOGGER.error(e.getMessage());
            }

        }
        return res;

    }

    @Override
    public boolean delete(@NotNull LessonRemainingTime obj) {
        int res = 0;

        if(!checkObject(obj))
            return false;

        try{
            String query = "DELETE FROM lesson_remaining_time WHERE id_message = ?";
            PreparedStatement ps = this.conn.prepareStatement(query);
            ps.setLong(1, obj.getId_message());

            res = ps.executeUpdate();

        } catch (SQLException e){
            LOGGER.error("Error when deleting message_id from lesson remaining time: "+obj.getId_message(),e);
        }

        return res==1;
    }

    @Override
    public boolean update(@NotNull LessonRemainingTime obj) {
        return false;
    }

    @NotNull
    @Override
    public ArrayList<LessonRemainingTime> findAll() {
        ArrayList<LessonRemainingTime> lessonsRemainingTime = new ArrayList<>();

        ResultSet results;

        try{
            String query = "SELECT id_lesson, id_message, id_channel FROM lesson_remaining_time";
            results = this.conn.createStatement().executeQuery(query);

            LessonDAO lessonDAO = (LessonDAO) DAOFactory.getLessonDAO();


            while(results.next()){

                lessonsRemainingTime.add(new LessonRemainingTime(
                                lessonDAO.find(results.getString(1)),
                                results.getLong(2),
                                results.getLong(3)));
            }






        }catch(SQLException e){
            LOGGER.error("Error when find all lesson remaining time object : "+e.getMessage(),e);
        }


        return lessonsRemainingTime;
    }

    private boolean checkObject(LessonRemainingTime lessonRemainingTime){
        System.out.println(lessonRemainingTime);
        if(lessonRemainingTime.getLesson() == null){
            LOGGER.error("Object Lesson in LessonRemainingTime is null");
            return false;
        }
        else if(lessonRemainingTime.getId_message() == null){
            LOGGER.error("Attribut id_message in LessonRemainingTime is null or empty");
            return false;
        }
        else if(lessonRemainingTime.getId_channel() == null){
            LOGGER.error("Attribut id_channel in LessonRemainingTime is null or empty");
            return false;
        }
        return true;
    }
}

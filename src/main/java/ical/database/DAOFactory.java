package ical.database;

import ical.database.dao.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;

/**
 * The DAO Factory class.
 * <br>Provides all DAO object for this application.
 *
 * @author Benoît Martel
 * @version 1.0
 */
public class DAOFactory {

    /**
     * the connection at the database
     */
    protected static final Connection conn = ConnectionSingleton.getInstance();

    /**
     * Get the guild DAO.
     *
     * @return the Guild DAO
     */
    @NotNull
    @Contract(" -> new")
    public static DAO<?> getGuildDAO(){
        return new GuildDAO(conn);
    }

    /**
     * Get the Professor DAO.
     *
     * @return the Professor DAO
     */
    @NotNull
    @Contract(" -> new")
    public static DAO<?> getProfessorDAO(){
        return new ProfessorDAO(conn);
    }

    /**
     * Get the Moved Lesson DAO.
     *
     * @return the Moved Lesson DAO
     */
    @NotNull
    @Contract(" -> new")
    public static DAO<?> getMovedLessonDAO(){
        return new MovedLessonDAO(conn);
    }

    /**
     * Get the Lesson DAO.
     *
     * @return the Lesson DAO
     */
    @NotNull
    @Contract(" -> new")
    public static DAO<?> getLessonDAO(){
        return new LessonDAO(conn);
    }

    /**
     * Get the Event Change DAO.
     *
     * @return the Event Change DAO
     */
    @NotNull
    @Contract(" -> new")
    public static DAO<?> getEventChange(){
        return new EventChangeDAO(conn);
    }

    /**
     * Get the Event Change Lesson DAO.
     *
     * @return the Event Change Lesson DAO
     */
    @NotNull
    @Contract(" -> new")
    public static DAO<?> getEventChange_Lesson(){
        return new EventChange_LessonDAO(conn);
    }

}
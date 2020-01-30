package ical.database;

import ical.database.dao.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;

public class DAOFactory {

    protected static final Connection conn = ConnectionSingleton.getInstance();

    @NotNull
    @Contract(" -> new")
    public static DAO<?> getGuildDAO(){
        return new GuildDAO(conn);
    }

    @NotNull
    @Contract(" -> new")
    public static DAO<?> getProfessorDAO(){
        return new ProfessorDAO(conn);
    }


    @NotNull
    @Contract(" -> new")
    public static DAO<?> getMovedLessonDAO(){
        return new MovedLessonDAO(conn);
    }

    @NotNull
    @Contract(" -> new")
    public static DAO<?> getLessonDAO(){
        return new LessonDAO(conn);
    }


    @NotNull
    @Contract(" -> new")
    public static DAO<?> getEventChange(){
        return new EventChangeDAO(conn);
    }

    @NotNull
    @Contract(" -> new")
    public static DAO<?> getEventChange_Lesson(){
        return new EventChange_LessonDAO(conn);
    }



}
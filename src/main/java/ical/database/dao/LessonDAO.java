package ical.database.dao;

import ical.database.DAOFactory;
import ical.database.entity.Lesson;
import ical.database.entity.Professor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.*;
import java.util.ArrayList;
import java.util.UUID;

public class LessonDAO extends DAO<Lesson> {

    public LessonDAO(@Nonnull Connection conn) {
        super(conn);
    }

    @Nullable
    @Override
    public Lesson create(@Nonnull Lesson obj) {

        Lesson res = null;

        ProfessorDAO professorDAO = (ProfessorDAO) DAOFactory.getProfessorDAO();
        Professor professor = professorDAO.find(obj.getProfessor().getName());
        if(professor == null)
            obj.setProfessor(professorDAO.create(obj.getProfessor()));
        else
            obj.setProfessor(professor);

        String unique_id = "LES"+ UUID.randomUUID().toString();


        try{
            String query = "INSERT INTO lesson(id, id_unique, nom, datedebut, datefin, description, classe, professor) VALUES (?,?,?,?,?,?,?,?)";
            PreparedStatement ps = this.conn.prepareStatement(query);
            ps.setString(1, unique_id);
            ps.setString(2, obj.getUID());
            ps.setString(3, obj.getName());
            ps.setTimestamp(4, new Timestamp(obj.getStartDate().getTime()));
            ps.setTimestamp(5, new Timestamp(obj.getEndDate().getTime()));
            ps.setString(6,obj.getDescription());
            ps.setString(7,obj.getRoom());
            ps.setInt(8,obj.getProfessor().getId());


            int insert = ps.executeUpdate();
            if(insert == 1){
                res = obj;
                res.setUniqueID(unique_id);
            }


        } catch (SQLException e){
            e.printStackTrace();
        }

        return res;

    }


    @Override
    public boolean delete(@Nonnull Lesson obj) {
        return false;
    }

    @Override
    public boolean update(@Nonnull Lesson obj) {
        return false;
    }

    @Nonnull
    @Override
    public ArrayList<Lesson> findAll() {
        return new ArrayList<>();
    }
}

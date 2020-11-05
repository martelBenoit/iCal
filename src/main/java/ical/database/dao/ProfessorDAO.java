package ical.database.dao;


import ical.database.entity.Professor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.*;
import java.util.ArrayList;

public class ProfessorDAO extends DAO<Professor>{

    private static final Logger LOGGER = LoggerFactory.getLogger(ProfessorDAO.class);


    public ProfessorDAO(Connection conn) {
        super(conn);
    }

    @Nullable
    public Professor create(@Nonnull Professor obj) {

        Professor professor = null;

        try{
            String query = "INSERT INTO professor(name, url, display_name) VALUES (?,?,?)";
            PreparedStatement ps = this.conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1,obj.getName());
            ps.setString(2,obj.getUrl());
            ps.setString(3,obj.getDisplayName());

            int affectedRows = ps.executeUpdate();

            if(affectedRows == 0)
                throw new SQLException("Creating professor failed, no rows affected.");

            try(ResultSet generatedKeys = ps.getGeneratedKeys()){
                if(generatedKeys.next()){
                    obj.setId(generatedKeys.getInt(3));
                    professor = obj;
                }
                else {
                    throw new SQLException("Creating professor failed, no ID obtained.");
                }
            }

        } catch (SQLException e){
            LOGGER.error(e.getMessage());
        }
        return professor;
    }

    @Override
    public boolean delete(@Nonnull Professor obj) {
        return false;
    }

    @Override
    public boolean update(@Nonnull Professor obj) {

        boolean updated = false;

        try{
            String query = "UPDATE professor SET name = ?, url = ?, display_name = ? WHERE id = ?";
            PreparedStatement ps = this.conn.prepareStatement(query);
            ps.setString(1,obj.getName());
            ps.setString(2,obj.getUrl());
            ps.setString(3,obj.getDisplayName());
            ps.setInt(4,obj.getId());

            int affectedRows = ps.executeUpdate();

            if(affectedRows == 0)
                throw new SQLException("Updating professor failed, no rows affected.");
            else
                updated = true;



        } catch (SQLException e){
            LOGGER.error(e.getMessage());
        }
        return updated;
    }

    @Nonnull
    @Override
    public ArrayList<Professor> findAll() {

        ResultSet results;

        ArrayList<Professor> professors = new ArrayList<>();
        Professor professor;

        try{
            String query = "SELECT id, name, url, display_name FROM professor";
            results = this.conn.createStatement().executeQuery(query);

            while(results.next()){
                professor = new Professor(
                        results.getInt(1),
                        results.getString(2),
                        results.getString(3),
                        results.getString(4)
                );
                professors.add(professor);

            }
        }catch(SQLException e){
            e.printStackTrace();
        }

        return professors;
    }

    @Nullable
    public Professor find(@Nonnull String name){
        ResultSet results;

        Professor professor = null;

        try{
            String query = "SELECT id, name, url, display_name FROM professor WHERE name = ?";
            PreparedStatement preparedStatement = this.conn.prepareStatement(query);
            preparedStatement.setString(1,name);
            results = preparedStatement.executeQuery();

            if(results.isBeforeFirst()){
                results.next();
                professor = new Professor(
                        results.getInt(1),
                        results.getString(2),
                        results.getString(3),
                        results.getString(4)
                );
            }


        }catch(SQLException e){
            e.printStackTrace();
        }

        return professor;
    }

    public ArrayList<Professor> searchByValue(String value){
        ResultSet results;

        ArrayList<Professor> professors = new ArrayList<>();
        Professor professor;


        try{
            String query = "SELECT id, name, url, display_name FROM professor WHERE UPPER(display_name) LIKE ? OR UPPER(name) LIKE ?";
            PreparedStatement preparedStatement = this.conn.prepareStatement(query);
            preparedStatement.setString(1,"%"+value.toUpperCase()+"%");
            preparedStatement.setString(2,"%"+value.toUpperCase()+"%");
            results = preparedStatement.executeQuery();

            LOGGER.debug(results.getStatement().toString());

            while(results.next()){

                professor = new Professor(
                        results.getInt(1),
                        results.getString(2),
                        results.getString(3),
                        results.getString(4)
                );
                professors.add(professor);

            }
        }catch(SQLException e){
            e.printStackTrace();
        }

        return professors;
    }

    @Nullable
    public Professor findById(int id){
        ResultSet results;

        Professor professor = null;

        try{
            String query = "SELECT id, name, url, display_name FROM professor WHERE id = ?";
            PreparedStatement preparedStatement = this.conn.prepareStatement(query);
            preparedStatement.setInt(1,id);
            results = preparedStatement.executeQuery();

            if(results.isBeforeFirst()){
                results.next();
                professor = new Professor(
                        results.getInt(1),
                        results.getString(2),
                        results.getString(3),
                        results.getString(4)
                );
            }


        }catch(SQLException e){
            e.printStackTrace();
        }

        return professor;
    }
}

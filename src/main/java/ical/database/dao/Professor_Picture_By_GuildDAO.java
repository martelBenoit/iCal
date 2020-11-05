package ical.database.dao;

import ical.database.DAOFactory;
import ical.database.entity.OGuild;
import ical.database.entity.Professor;
import ical.database.entity.Professor_Picture_By_Guild;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;

/**
 * Professor_Picture_By_GuildDAO class.
 *
 * @author Benoît Martel
 * @version 1.0
 * @since 1.9
 */
public class Professor_Picture_By_GuildDAO extends DAO<Professor_Picture_By_Guild> {

    /**
     * Constructor.
     *
     * @param conn the sql connection
     */
    public Professor_Picture_By_GuildDAO(@NotNull Connection conn) {
        super(conn);
    }

    @Nullable
    @Override
    public Professor_Picture_By_Guild create(@NotNull Professor_Picture_By_Guild obj) {

        Professor_Picture_By_Guild object = null;

        try{
            String query = "INSERT INTO professor_Picture_By_Guild(id_guild, id_professor, url, modification_author) VALUES (?,?,?,?)";
            PreparedStatement ps = this.conn.prepareStatement(query);
            ps.setString(1,obj.getGuild().getIdGuild());
            ps.setInt(2,obj.getProfessor().getId());
            ps.setString(3,obj.getUrl());
            ps.setString(4,obj.getId_user());

            if(ps.executeUpdate()==1) {
                object = findByGuildAndProfessor(obj.getGuild(), obj.getProfessor());
            }

        } catch (SQLException e){
            LOGGER.error("Error when inserting professor picture by guild : "+obj.getGuild().getIdGuild()+", "+obj.getProfessor().getId(),e);
        }

        return object;
    }

    @Nullable
    public Professor_Picture_By_Guild findByGuildAndProfessor(@NotNull OGuild guild, @NotNull Professor professor){

        ResultSet result;
        Professor_Picture_By_Guild ret = null;

        try{
            String query = "SELECT id_guild, id_professor, url, modification_author, last_modification " +
                    "FROM professor_Picture_By_Guild " +
                    "WHERE id_guild = ? AND id_professor = ?";

            PreparedStatement ps = this.conn.prepareStatement(query);
            ps.setString(1,guild.getIdGuild());
            ps.setInt(2,professor.getId());

            GuildDAO guildDAO = (GuildDAO) DAOFactory.getGuildDAO();
            ProfessorDAO professorDAO = (ProfessorDAO) DAOFactory.getProfessorDAO();

            result = ps.executeQuery();
            if(result.next())
                ret = new Professor_Picture_By_Guild(
                        guildDAO.find(result.getString(1)),
                        professorDAO.findById(result.getInt(2)),
                        result.getString(3),
                        result.getString(4),
                        result.getTimestamp(5));
        } catch (SQLException e){
            LOGGER.error("Error while searching the Professor_Picture_By_Guild : "+guild.getIdGuild()+", "+professor.getId(),e);
            ret = null;
        }

        return ret;
    }

    @Override
    public boolean delete(@NotNull Professor_Picture_By_Guild obj) {
        return false;
    }

    @Override
    public boolean update(@NotNull Professor_Picture_By_Guild obj) {

        if(findByGuildAndProfessor(obj.getGuild(),obj.getProfessor()) != null) {

            boolean updated = false;

            try {
                Timestamp last_modification = new Timestamp(new Date().getTime());
                String query = "UPDATE professor_picture_by_guild SET url = ?, modification_author = ?, last_modification = ? WHERE id_guild = ? AND id_professor = ?";
                PreparedStatement ps = this.conn.prepareStatement(query);
                ps.setString(1, obj.getUrl());
                ps.setString(2, obj.getId_user());
                ps.setTimestamp(3, last_modification);
                ps.setString(4, obj.getGuild().getIdGuild());
                ps.setInt(5, obj.getProfessor().getId());

                int affectedRows = ps.executeUpdate();

                if (affectedRows == 0)
                    throw new SQLException("Updating Professor_Picture_By_Guild failed, no rows affected.");
                else {
                    updated = true;
                    obj.setLast_modification(last_modification);
                }

            } catch (SQLException e) {
                LOGGER.error(e.getMessage());
            }
            return updated;

        }
        else {
            obj = create(obj);
            return obj != null;
        }


    }

    @NotNull
    @Override
    public ArrayList<Professor_Picture_By_Guild> findAll() {
        return null;
    }

}

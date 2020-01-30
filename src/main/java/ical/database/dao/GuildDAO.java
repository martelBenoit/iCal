package ical.database.dao;

import ical.database.entity.OGuild;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.*;
import java.util.ArrayList;

public class GuildDAO extends DAO<OGuild> {

    public GuildDAO(Connection conn) {
        super(conn);
    }

    @Nullable
    @Override
    public OGuild create(@Nonnull OGuild obj) {

        OGuild guild = null;

        try{
            String query = "INSERT INTO guild(idguild, idchannel, urlplanning) VALUES (?,?,?)";
            PreparedStatement ps = this.conn.prepareStatement(query);
            ps.setString(1,obj.getIdGuild());
            ps.setString(2,obj.getIdChannel());
            ps.setString(3,obj.getUrlSchedule());

            if(ps.executeUpdate()==1)
                guild = obj;

        } catch (SQLException e){
            LOGGER.error("Error when inserting guild id : "+obj.getIdGuild(),e);
        }

        return guild;
    }

    @Override
    public boolean delete(@Nonnull OGuild obj) {
        boolean res = false;

        try{
            String query = "DELETE FROM guild WHERE idguild = ?";
            PreparedStatement ps = this.conn.prepareStatement(query);
            ps.setString(1, obj.getIdGuild());

            res = ps.execute();

        } catch (SQLException e){
            LOGGER.error("Error when deleting guild id : "+obj.getIdGuild(),e);
        }

        return res;
    }

    @Override
    public boolean update(@Nonnull OGuild obj) {

        int res = -1;

        try{
            String query = "UPDATE guild SET idchannel = ?, urlplanning = ?, modifnotif = ?, lessonnotif = ? WHERE idguild = ?";
            PreparedStatement ps = this.conn.prepareStatement(query);

            ps.setString(1,obj.getIdChannel());
            ps.setString(2,obj.getUrlSchedule());
            ps.setBoolean(3,obj.modifNotifisEnabled());
            ps.setBoolean(4,obj.lessonNotifisEnabled());
            ps.setString(5, obj.getIdGuild());

            res = ps.executeUpdate();

        } catch (SQLException e){
            LOGGER.error("Error when updating a guild id "+obj.getIdGuild(),e);
        }

        return res==1;
    }

    @Nullable
    public OGuild find(@Nonnull String idGuild){
        ResultSet result;
        OGuild guild = null;

        try{
            String query = "SELECT idguild, idchannel, urlplanning, modifnotif, lessonnotif FROM guild WHERE idGuild = ?";
            PreparedStatement ps = this.conn.prepareStatement(query);
            ps.setString(1,idGuild);

            result = ps.executeQuery();
            if(result.next())
                guild = new OGuild(
                        result.getString(1),
                        result.getString(2),
                        result.getString(3),
                        result.getBoolean(4),
                        result.getBoolean(5));
        } catch (SQLException e){
            LOGGER.error("Error while searching the guild : "+idGuild,e);
            guild = null;
        }

        return guild;
    }

    @NotNull
    @Override
    public ArrayList<OGuild> findAll() {

        ResultSet results;

        ArrayList<OGuild> guilds = new ArrayList<>();

        try{
            String query = "SELECT idguild, idchannel, urlplanning, modifnotif, lessonnotif FROM guild";
            results = this.conn.createStatement().executeQuery(query);

            while(results.next()){
                guilds.add(new OGuild(
                        results.getString(1),
                        results.getString(2),
                        results.getString(3),
                        results.getBoolean(4),
                        results.getBoolean(5)));
            }

        }catch(SQLException e){
            LOGGER.error("Error when find all guild : "+e.getMessage(),e);
        }

        return guilds;
    }
}

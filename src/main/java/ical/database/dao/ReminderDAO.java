package ical.database.dao;


import ical.database.DAOFactory;
import ical.database.entity.OGuild;
import ical.database.entity.Reminder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.User;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.*;
import java.util.ArrayList;

/**
 * @since 1.8
 */
public class ReminderDAO extends DAO<Reminder> {

    private final GuildDAO guildDAO = (GuildDAO) DAOFactory.getGuildDAO();

    public ReminderDAO(@Nonnull Connection conn) {
        super(conn);
    }

    @Nullable
    @Override
    public Reminder create(@Nonnull Reminder obj) {

        Reminder reminder = null;

        try{
            String query = "INSERT INTO reminder(name, timestamp, created_at, author, recipient, guild) VALUES (?,?,?,?,?,?)";
            PreparedStatement ps = this.conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1,obj.getName());
            ps.setTimestamp(2,new Timestamp(obj.getDate().getTime()));
            ps.setTimestamp(3,new Timestamp(obj.getCreated_at().getTime()));
            ps.setString(4,obj.getAuthor());
            ps.setString(5,obj.getRecipient());
            if(obj.getGuild() != null)
                ps.setString(6,obj.getGuild().getIdGuild());
            else
                ps.setString(6,null);

            int affectedRows = ps.executeUpdate();

            if(affectedRows == 0)
                throw new SQLException("Creating reminder failed, no rows affected.");

            try(ResultSet generatedKeys = ps.getGeneratedKeys()){
                if(generatedKeys.next()){
                    obj.setId(generatedKeys.getInt(1));
                    reminder = obj;
                }
                else {
                    throw new SQLException("Creating reminder failed, no ID obtained.");
                }
            }

        } catch (SQLException e){
            LOGGER.error(e.getMessage());
        }
        return reminder;
    }

    @Override
    public boolean delete(@Nonnull Reminder obj) {

        int res = 0;

        if(obj.getId() > 0) {

            try {
                String query = "DELETE FROM reminder WHERE id = ?";
                PreparedStatement ps = this.conn.prepareStatement(query);
                ps.setInt(1, obj.getId());

                res = ps.executeUpdate();

            } catch (SQLException e) {
                LOGGER.error("Error when deleting reminder : " + obj.getId(), e);
            }

        }

        return res==1;
    }

    @Override
    public boolean update(@Nonnull Reminder obj) {
        return false;
    }

    @Nonnull
    @Override
    public ArrayList<Reminder> findAll() {

        ResultSet results;

        ArrayList<Reminder> reminders = new ArrayList<>();

        try{
            String query = "SELECT id, name, timestamp, created_at, author, recipient, guild FROM reminder";
            results = this.conn.createStatement().executeQuery(query);

            while(results.next()){

                OGuild guild = null;
                if(results.getString(7) != null){
                    guild = guildDAO.find(results.getString(7));
                }

                reminders.add(new Reminder(
                        results.getInt(1),
                        results.getString(2),
                        results.getTimestamp(3),
                        results.getTimestamp(4),
                        results.getString(5),
                        results.getString(6),
                        guild));
            }

        }catch(SQLException e){
            LOGGER.error("Error when find all reminders : "+e.getMessage(),e);
        }

        return reminders;
    }

    public Reminder findById(int id){

        try{

            PreparedStatement preparedStatement = this.conn.prepareStatement("SELECT id, name, timestamp, created_at, author, recipient, guild FROM reminder WHERE id = ?");
            preparedStatement.setInt(1,id);

            ResultSet results = preparedStatement.executeQuery();

            if(!results.next()){
                return null;
            }
            else{
                OGuild guild = null;
                if(results.getString(7) != null){
                    guild = guildDAO.find(results.getString(7));
                }
                return new Reminder(
                        results.getInt(1),
                        results.getString(2),
                        results.getTimestamp(3),
                        results.getTimestamp(4),
                        results.getString(5),
                        results.getString(6),
                        guild
                );
            }

        }catch(SQLException e){
            LOGGER.error("Error when find reminder id number "+id+" : "+e.getMessage(),e);
            return null;
        }

    }

    public ArrayList<Reminder> findByAuthor(User user){
        String query = "SELECT id, name, timestamp, created_at, author, recipient, guild FROM reminder WHERE author = ?";
        return findByUser(user, query);

    }

    public ArrayList<Reminder> findByGuild(Guild guild){

        String query = "SELECT id, name, timestamp, created_at, author, recipient, guild FROM reminder WHERE guild = ?";
        return findByUser(guild, query);

    }


    public ArrayList<Reminder> findByRecipient(User user){
        String query = "SELECT id, name, timestamp, created_at, author, recipient, guild FROM reminder WHERE recipient = ?";
        return findByUser(user, query);
    }

    private ArrayList<Reminder> findByUser(ISnowflake snowflake, String query){

        ResultSet results;

        ArrayList<Reminder> reminders = new ArrayList<>();

        try{

            PreparedStatement preparedStatement = this.conn.prepareStatement(query);
            preparedStatement.setString(1,snowflake.getId());

            results = preparedStatement.executeQuery();

            while(results.next()){

                OGuild guild = null;
                if(results.getString(7) != null){
                    guild = guildDAO.find(results.getString(7));
                }

                reminders.add(new Reminder(
                        results.getInt(1),
                        results.getString(2),
                        results.getTimestamp(3),
                        results.getTimestamp(4),
                        results.getString(5),
                        results.getString(6),
                        guild));


            }

        }catch(SQLException e){
            LOGGER.error("Error when find reminders : "+e.getMessage(),e);
        }

        return reminders;

    }


}
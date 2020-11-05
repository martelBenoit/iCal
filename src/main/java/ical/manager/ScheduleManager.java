package ical.manager;

import ical.core.RoomSchedule;
import ical.core.Schedule;
import ical.database.DAOFactory;
import ical.database.dao.GuildDAO;
import ical.database.dao.ProfessorDAO;
import ical.database.dao.Professor_Picture_By_GuildDAO;
import ical.database.entity.Lesson;
import ical.database.entity.OGuild;
import ical.database.entity.Professor;
import ical.database.entity.Professor_Picture_By_Guild;

import java.util.HashMap;

/**
 * Class ScheduleManager.
 * <br>Allow to manage the different schedule.
 *
 * @author Benoît Martel
 * @version 1.0
 */
public class ScheduleManager {

    private final RoomSchedule roomSchedule;

    /**
     * Each schedule has a id guild represented by this HashMap.
     */
    private final HashMap<String, Schedule> schedules;

    /**
     * Constructor.
     * <br>Initializes the HashMap.
     */
    public ScheduleManager(){

        this.schedules = new HashMap<>();
        this.roomSchedule = new RoomSchedule();
    }

    /**
     * Add a new schedule to the hashMap.
     *
     * @param idGuild id of the guild for the schedule.
     * @param schedule the schedule.
     * @return true if the schedule has correctly added
     */
    public boolean addSchedule(String idGuild, Schedule schedule){
        int ret = this.schedules.size();
        this.schedules.put(idGuild,schedule);
        return this.schedules.size()==(ret+1);
    }

    /**
     * Get the schedule for the specified guild.
     *
     * @param guildID the id of the guild to get his schedule
     * @return the schedule of the guild
     */
    public Schedule getSchedule(String guildID){
        return this.schedules.get(guildID);
    }

    /**
     * Get all schedules.
     *
     * @return a list of schedules
     */
    public HashMap<String, Schedule> getSchedules(){
        return schedules;
    }

    /**
     * Remove the schedule for the specified guild.
     *
     * @param idGuild the guild id to remove his schedule
     * @return true if the schedule has correctly removed
     */
    public boolean removeSchedule(String idGuild){
        int ret = this.schedules.size();
        this.schedules.remove(idGuild);
        return this.schedules.size()==ret-1;
    }

    public RoomSchedule getRoomSchedule(){
        return roomSchedule;
    }

    public void updatePP(String idGuild){

        Schedule schedule = getSchedule(idGuild);
        GuildDAO guildDAO = (GuildDAO) DAOFactory.getGuildDAO();
        ProfessorDAO professorDAO = (ProfessorDAO) DAOFactory.getProfessorDAO();
        Professor_Picture_By_GuildDAO professorPictureByGuildDAO = (Professor_Picture_By_GuildDAO) DAOFactory.getProfessorPictureByGuild();

        OGuild guild = guildDAO.find(idGuild);
        if(guild != null){
            if(guild.usingSpecificPPGranted()){
                for(Lesson lesson : schedule.getLessons()){
                    Professor actualProfessor = lesson.getProfessor();
                    Professor_Picture_By_Guild professorPictureByGuild = professorPictureByGuildDAO.findByGuildAndProfessor(guild,actualProfessor);
                    if(professorPictureByGuild != null){
                        actualProfessor.setUrl(professorPictureByGuild.getUrl());
                    }
                }
            }
            else{
                for(Lesson lesson : schedule.getLessons()){
                    Professor actualProfessor = lesson.getProfessor();
                    Professor defaultProfessor = professorDAO.findById(actualProfessor.getId());
                    if(defaultProfessor != null){
                        actualProfessor.setUrl(defaultProfessor.getUrl());
                    }
                }
            }
        }
    }

}
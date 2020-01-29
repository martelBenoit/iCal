package ical.manager;

import ical.core.Schedule;

import java.util.HashMap;

/**
 * Class ScheduleManager.
 * <br>Allow to manage the different schedule.
 *
 * @author Benoît Martel
 * @version 1.0
 */
public class ScheduleManager {

    /**
     * Each schedule has a id guild represented by this HashMap.
     */
    private HashMap<String, Schedule> schedules;

    /**
     * Constructor.
     * <br>Initializes the HashMap.
     */
    public ScheduleManager(){
        this.schedules = new HashMap<>();
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
     * @param idGuild the id of the guild to get his schedule
     * @return the schedule of the guild
     */
    public Schedule getSchedule(String idGuild){
        return this.schedules.get(idGuild);
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

}
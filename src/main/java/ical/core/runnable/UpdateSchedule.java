package ical.core.runnable;

import ical.core.Schedule;
import ical.manager.ScheduleManager;
import net.fortuna.ical4j.data.ParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.ParseException;
import java.util.Map;

public class UpdateSchedule implements Runnable {

    private ScheduleManager scheduleManager;

    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateSchedule.class);


    public UpdateSchedule(ScheduleManager scheduleManager){
        this.scheduleManager = scheduleManager;
    }

    @Override
    public void run() {

        try{
            for (Map.Entry<String, Schedule> e : this.scheduleManager.getSchedules().entrySet()) {
                Schedule schedule = e.getValue();
                schedule.updateLessons();
            }
            LOGGER.info("Schedule update");
        } catch (ParseException | IOException | ParserException e) {
            LOGGER.error("Error when updating the schedule");
            e.printStackTrace();
        }
    }
}

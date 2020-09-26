package ical.core.runnable;

import ical.core.Schedule;
import ical.manager.ScheduleManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.fortuna.ical4j.data.ParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.ParseException;
import java.util.Map;

public class UpdateSchedule implements Runnable {

    private ScheduleManager scheduleManager;

    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateSchedule.class);

    private JDA jda;


    public UpdateSchedule(ScheduleManager scheduleManager, JDA jda){
        this.scheduleManager = scheduleManager;
        this.jda = jda;
    }

    @Override
    public void run() {

        try {
            for (Map.Entry<String, Schedule> e : this.scheduleManager.getSchedules().entrySet()) {
                Schedule schedule = e.getValue();
                schedule.updateEntries();
            }
            this.scheduleManager.getRoomSchedule().updateEntries();
            LOGGER.info("Schedule update");
            jda.getPresence().setPresence(OnlineStatus.ONLINE, true);
        } catch (ParseException | IOException e) {
            LOGGER.error("Error when updating the schedule");
            jda.getPresence().setPresence(OnlineStatus.IDLE, true);
            e.printStackTrace();
        } catch (ParserException parser) {
            LOGGER.error("Parser error : " + parser.getMessage());
            jda.getPresence().setPresence(OnlineStatus.IDLE, true);
        }


    }

}

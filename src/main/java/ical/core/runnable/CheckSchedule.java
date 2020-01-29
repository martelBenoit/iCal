package ical.core.runnable;

import ical.database.entity.Lesson;
import ical.core.Schedule;
import ical.database.entity.MovedLesson;
import ical.manager.ScheduleManager;
import ical.database.DAOFactory;
import ical.database.dao.GuildDAO;
import ical.database.entity.OEventChange;
import ical.database.entity.OGuild;
import ical.util.ModificationType;
import ical.util.Notification;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class CheckSchedule implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(CheckSchedule.class);

    private ScheduleManager scheduleManager;
    private JDA jda;

    public CheckSchedule(JDA jda, ScheduleManager scheduleManager) {
        this.scheduleManager = scheduleManager;
        this.jda = jda;
    }

    @Override
    public void run() {

        //LOGGER.info("Check schedule");

        for (Map.Entry<String, Schedule> e : this.scheduleManager.getSchedules().entrySet()) {

            Schedule schedule = e.getValue();
            ArrayList<Lesson> nextLessons = schedule.getNextLessons();
            String idGuild = e.getKey();

            GuildDAO guildDAO = (GuildDAO) DAOFactory.getGuildDAO();
            OGuild guild = guildDAO.find(idGuild);

            if(guild.lessonNotifisEnabled()){
                if (nextLessons.get(0).timeRemainingInSeconds() <= 900) {
                    if (!schedule.hasAlreadyBeenNotified()) {
                        schedule.setNotified(true);

                        MessageEmbed message = Notification.prepareNotificationNextLessons(nextLessons);
                        if (guild != null)
                            Objects.requireNonNull(jda.getTextChannelById(guild.getIdChannel())).sendMessage(message).queue();
                    }
                }
                else
                    schedule.setNotified(false);
            }
            else
                schedule.setNotified(false);


            if(guild.modifNotifisEnabled()) {

                // On récupère les changements
                ArrayList<MovedLesson> addedLessons = schedule.getAddedLessons();
                ArrayList<MovedLesson> removedLessons = schedule.getRemovedLessons();

                List<MovedLesson> movedLessons = schedule.getMovedLessons();


                if (!movedLessons.isEmpty()) {

                    OEventChange eventChange = new OEventChange(UUID.randomUUID().toString(), new Date(), ModificationType.MOVE);
                    MessageEmbed me = Notification.prepareNotificationModificationsLessons(eventChange, movedLessons, schedule.getCreationDate());

                    if (guild != null)
                        Objects.requireNonNull(jda.getTextChannelById(guild.getIdChannel())).sendMessage(me).queue();

                }

                if (!addedLessons.isEmpty()) {


                    OEventChange eventChange = new OEventChange(UUID.randomUUID().toString(), new Date(), ModificationType.ADD);
                    MessageEmbed me = Notification.prepareNotificationModificationsLessons(eventChange, addedLessons, schedule.getCreationDate());

                    if (guild != null){
                        Objects.requireNonNull(jda.getTextChannelById(guild.getIdChannel())).sendMessage(me).queue();
                    }

                }

                if (!removedLessons.isEmpty()) {

                    OEventChange eventChange = new OEventChange(UUID.randomUUID().toString(), new Date(), ModificationType.REMOVE);
                    MessageEmbed me = Notification.prepareNotificationModificationsLessons(eventChange, removedLessons, schedule.getCreationDate());

                    if (guild != null)
                        Objects.requireNonNull(jda.getTextChannelById(guild.getIdChannel())).sendMessage(me).queue();
                }

                // Permet de re init le planning actuel.
                schedule.resetPreviousLessons();
            }

        }
    }
}

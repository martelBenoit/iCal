package ical.core.runnable;

import ical.database.entity.*;
import ical.core.Schedule;
import ical.manager.ScheduleManager;
import ical.database.DAOFactory;
import ical.database.dao.GuildDAO;
import ical.manager.TaskScheduler;
import ical.util.ModificationType;
import ical.util.Notification;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class CheckSchedule implements Runnable {


    private ScheduleManager scheduleManager;
    private JDA jda;

    private static final Logger LOGGER = LoggerFactory.getLogger(CheckSchedule.class);

    public CheckSchedule(JDA jda, ScheduleManager scheduleManager) {
        this.scheduleManager = scheduleManager;
        this.jda = jda;
    }

    @Override
    public void run() {

        try {

            for (Map.Entry<String, Schedule> e : this.scheduleManager.getSchedules().entrySet()) {

                Schedule schedule = e.getValue();
                ArrayList<Lesson> nextLessons = schedule.getNextLessons();
                String idGuild = e.getKey();

                GuildDAO guildDAO = (GuildDAO) DAOFactory.getGuildDAO();
                OGuild guild = guildDAO.find(idGuild);


                // On vérifie que l'on a bien récupéré l'objet de la base de données
                if (guild != null) {

                    String idChannel = guild.getIdChannel();

                    if (idChannel != null) {

                        TextChannel channel = jda.getTextChannelById(idChannel);

                        if (channel != null && channel.canTalk()) {

                            // PARTIE NOTIFICATION PROCHAIN COURS
                            if (guild.lessonNotifisEnabled()) {
                                if (nextLessons.get(0).timeRemainingInSeconds() <= 900) {
                                    if (!schedule.hasAlreadyBeenNotified()) {
                                        MessageEmbed message = Notification.prepareNotificationNextLessons(nextLessons);
                                        if (message != null) {
                                            channel.sendMessage(message).queue((messageEmbed -> messageEmbed.delete().queueAfter(
                                                    nextLessons.get(0).timeRemainingInSeconds() * 2,
                                                    TimeUnit.SECONDS
                                            )));
                                            schedule.setNotified(true);
                                        } else
                                            LOGGER.error("Error when generating the message for the course reminder");


                                    }
                                } else
                                    schedule.setNotified(false);
                            }

                            // PARTIE NOTIFICATION CHANGEMENT EMPLOI DU TEMPS
                            if (guild.modifNotifisEnabled()) {

                                // On récupère les changements
                                ArrayList<MovedLesson> addedLessons = schedule.getAddedLessons();
                                ArrayList<MovedLesson> removedLessons = schedule.getRemovedLessons();

                                List<MovedLesson> movedLessons = schedule.getMovedLessons();


                                if (!movedLessons.isEmpty()) {

                                    OEventChange eventChange = new OEventChange(
                                            UUID.randomUUID().toString(),
                                            new Date(),
                                            ModificationType.MOVE
                                    );
                                    MessageEmbed me = Notification.prepareNotificationModificationsLessons(
                                            eventChange,
                                            movedLessons,
                                            schedule.getCreationDate()
                                    );
                                    if (channel.getId().equals("666745599059689517")) {
                                        channel.sendMessage(Objects.requireNonNull(jda.getRoleById("619629144258379787"))
                                                .getAsMention())
                                                .queue();
                                    }
                                    channel.sendMessage(me).queue();

                                }

                                if (!addedLessons.isEmpty()) {

                                    OEventChange eventChange = new OEventChange(
                                            UUID.randomUUID().toString(),
                                            new Date(),
                                            ModificationType.ADD);
                                    MessageEmbed me = Notification.prepareNotificationModificationsLessons(
                                            eventChange,
                                            addedLessons,
                                            schedule.getCreationDate()
                                    );
                                    channel.sendMessage(me).queue();

                                }

                                if (!removedLessons.isEmpty()) {

                                    OEventChange eventChange = new OEventChange(
                                            UUID.randomUUID().toString(),
                                            new Date(),
                                            ModificationType.REMOVE
                                    );
                                    MessageEmbed me = Notification.prepareNotificationModificationsLessons(
                                            eventChange,
                                            removedLessons,
                                            schedule.getCreationDate()
                                    );
                                    channel.sendMessage(me).queue();

                                }

                                // Permet de re init le planning actuel.
                                schedule.resetPreviousLessons();
                            }

                        }
                    }
                }
            }

        }catch (Exception e){
            LOGGER.error(e.getMessage(),e.fillInStackTrace());
        }
    }
}

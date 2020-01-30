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
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.*;

public class CheckSchedule implements Runnable {

    private ScheduleManager scheduleManager;
    private JDA jda;

    public CheckSchedule(JDA jda, ScheduleManager scheduleManager) {
        this.scheduleManager = scheduleManager;
        this.jda = jda;
    }

    @Override
    public void run() {

        for (Map.Entry<String, Schedule> e : this.scheduleManager.getSchedules().entrySet()) {

            Schedule schedule = e.getValue();
            ArrayList<Lesson> nextLessons = schedule.getNextLessons();
            String idGuild = e.getKey();

            GuildDAO guildDAO = (GuildDAO) DAOFactory.getGuildDAO();
            OGuild guild = guildDAO.find(idGuild);

            // On vérifie que l'on a bien récupéré l'objet de la base de donnée
            if (guild != null) {

                String idChannel = guild.getIdChannel();

                if (idChannel != null) {

                    TextChannel channel = jda.getTextChannelById(idChannel);

                    if (channel != null && channel.canTalk()) {

                        // PARTIE NOTIFICATION PROCHAIN COURS
                        if (guild.lessonNotifisEnabled()) {
                            if (nextLessons.get(0).timeRemainingInSeconds() <= 900)
                                if (!schedule.hasAlreadyBeenNotified()) {
                                    channel.sendTyping().queue();
                                    schedule.setNotified(true);
                                    MessageEmbed message = Notification.prepareNotificationNextLessons(nextLessons);
                                    channel.sendMessage(message).queue();
                                } else
                                    schedule.setNotified(false);
                            else
                                schedule.setNotified(false);
                        }

                        // PARTIE NOTIFICATION CHANGEMENT EMPLOI DU TEMPS
                        if (guild.modifNotifisEnabled()) {

                            // On récupère les changements
                            ArrayList<MovedLesson> addedLessons = schedule.getAddedLessons();
                            ArrayList<MovedLesson> removedLessons = schedule.getRemovedLessons();

                            List<MovedLesson> movedLessons = schedule.getMovedLessons();

                            if (!movedLessons.isEmpty()) {

                                OEventChange eventChange = new OEventChange(UUID.randomUUID().toString(), new Date(), ModificationType.MOVE);
                                MessageEmbed me = Notification.prepareNotificationModificationsLessons(eventChange, movedLessons, schedule.getCreationDate());
                                channel.sendMessage(me).queue();

                            }

                            if (!addedLessons.isEmpty()) {

                                OEventChange eventChange = new OEventChange(UUID.randomUUID().toString(), new Date(), ModificationType.ADD);
                                MessageEmbed me = Notification.prepareNotificationModificationsLessons(eventChange, addedLessons, schedule.getCreationDate());
                                channel.sendMessage(me).queue();

                            }

                            if (!removedLessons.isEmpty()) {

                                OEventChange eventChange = new OEventChange(UUID.randomUUID().toString(), new Date(), ModificationType.REMOVE);
                                MessageEmbed me = Notification.prepareNotificationModificationsLessons(eventChange, removedLessons, schedule.getCreationDate());
                                channel.sendMessage(me).queue();

                            }

                            // Permet de re init le planning actuel.
                            schedule.resetPreviousLessons();
                        }

                    }
                }
            }
        }
    }
}



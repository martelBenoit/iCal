package ical.core.runnable;

import ical.core.Schedule;
import ical.database.DAOFactory;
import ical.database.dao.GuildDAO;
import ical.database.entity.Lesson;
import ical.database.entity.OGuild;
import ical.graphic.Timetable;
import ical.manager.ScheduleManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.InputStream;
import java.time.Instant;
import java.util.*;

public class WeekInformationPlanning implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(WeekInformationPlanning.class);

    private JDA jda;
    private ScheduleManager scheduleManager;

    public WeekInformationPlanning(JDA jda, ScheduleManager scheduleManager){
        this.jda = jda;
        this.scheduleManager = scheduleManager;
    }


    @Override
    public void run() {

        LOGGER.info("WeekInformationPlanning started");

        for (Map.Entry<String, Schedule> e : this.scheduleManager.getSchedules().entrySet()) {

            Schedule schedule = e.getValue();
            String idGuild = e.getKey();

            GuildDAO guildDAO = (GuildDAO) DAOFactory.getGuildDAO();
            OGuild guild = guildDAO.find(idGuild);

            // On vérifie que l'on a bien récupéré l'objet de la base de données
            if (guild != null) {

                String idChannel = guild.getIdChannel();

                if (idChannel != null) {

                    TextChannel channel = jda.getTextChannelById(idChannel);

                    if (channel != null && channel.canTalk()) {

                        final ArrayList<Lesson> lessons = schedule.getWeekLessons();
                        if (lessons.size() > 0) {
                            Timetable timetable = new Timetable(lessons);
                            InputStream inputStream = timetable.generateTimetable();
                            String nameImg = "WEEK_" + UUID.randomUUID() + ".png";
                            if (inputStream != null) {
                                final EmbedBuilder eb = new EmbedBuilder();
                                eb.setTitle("Les cours pr\u00e9vus cette semaine  ", null);
                                eb.setColor(new Color(238358));
                                eb.setImage("attachment://" + nameImg);
                                eb.setTimestamp(schedule.getCreationDate());
                                eb.setFooter("ENT","https://www-ensibs.univ-ubs.fr/skins/ENSIBS/resources/img/logo.png");

                                channel.sendMessage(eb.build()).addFile(inputStream, nameImg).queue();
                            }
                        }

                    }
                }
            }
        }

    }
}

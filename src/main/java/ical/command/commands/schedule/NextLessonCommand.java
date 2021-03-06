package ical.command.commands.schedule;

import ical.command.GuildCommandContext;
import ical.command.commands.AbstractScheduleCommand;
import ical.database.DAOFactory;
import ical.database.dao.LessonRemainingTimeDAO;
import ical.database.entity.Lesson;
import ical.database.entity.LessonRemainingTime;
import ical.manager.ScheduleManager;
import ical.util.Config;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.util.ArrayList;

/**
 * NextLessonCommand class.
 *
 * @author Benoît Martel
 * @version 1.0
 * @since 1.0
 */
public class NextLessonCommand extends AbstractScheduleCommand {

    /**
     * Default constructor.
     *
     * @param scheduleManager the schedule manager
     */
    public NextLessonCommand(ScheduleManager scheduleManager) {
        super(scheduleManager);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handle(GuildCommandContext ctx) {

            MessageChannel channel = ctx.getChannel();

            ArrayList<Lesson> lessons = scheduleManager.getSchedule(ctx.getGuild().getId()).getNextLessons();

            if (lessons.size() == 1) {
                final EmbedBuilder eb = new EmbedBuilder();
                eb.setTitle("  " + lessons.get(0).getName() + "  dans " + lessons.get(0).timeRemaining(), null);
                eb.setColor(new Color(238358));
                eb.setThumbnail(lessons.get(0).getProfessor().getUrl());
                eb.addField("Date", lessons.get(0).getDay(), false);
                eb.addField("Début du cours", lessons.get(0).getStartTime(), true);
                eb.addField("Fin du cours", lessons.get(0).getEndTime(), true);
                eb.addField("Salle", lessons.get(0).getRoom(), false);
                eb.addField("Plus d'infos", lessons.get(0).getDescription(), false);

                MessageEmbed embedMessage = eb.build();

                channel.sendMessage(embedMessage).queue(message -> {
                    LessonRemainingTime lessonRemainingTime =
                            new LessonRemainingTime(lessons.get(0), message.getIdLong(), message.getChannel().getIdLong());
                    LessonRemainingTimeDAO lessonRemainingTimeDAO =
                            (LessonRemainingTimeDAO) DAOFactory.getLesson_Remaining_Time();
                    lessonRemainingTimeDAO.create(lessonRemainingTime);
                });
            } else if (lessons.size() > 1) {
                final EmbedBuilder eb = new EmbedBuilder();
                eb.setTitle("  Plusieurs cours à venir dans " + lessons.get(0).timeRemaining(), null);
                eb.setColor(new Color(238358));
                eb.addField("Date", lessons.get(0).getDay(), false);
                eb.addField("Début des cours", lessons.get(0).getStartTime(), false);
                for (Lesson lesson : lessons) {
                    String description = "Fin du cours : " + lesson.getEndTime() + "\n" +
                            "Salle : " + lesson.getRoom() + "\n" +
                            "Plus d'infos : " + lesson.getDescription();
                    eb.addField(lesson.getName(), description, true);
                }
                MessageEmbed embedMessage = eb.build();
                channel.sendMessage(embedMessage).queue(message -> {
                    LessonRemainingTime lessonRemainingTime =
                            new LessonRemainingTime(lessons.get(0), message.getIdLong(), message.getChannel().getIdLong());
                    LessonRemainingTimeDAO lessonRemainingTimeDAO =
                            (LessonRemainingTimeDAO) DAOFactory.getLesson_Remaining_Time();
                    lessonRemainingTimeDAO.create(lessonRemainingTime);
                });
            } else {
                channel.sendMessage("Pas de prochain cours").queue();
            }


    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "nextLesson";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getHelp() {
        return "Affiche le/les prochain(s) cours (si plusieurs en même temps).\n"+
                "Utilisation : `"+ Config.get("prefix")+getName()+"`";
    }

}

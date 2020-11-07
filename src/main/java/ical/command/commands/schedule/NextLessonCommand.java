package ical.command.commands.schedule;

import ical.command.GuildCommandContext;
import ical.command.commands.AbstractScheduleCommand;
import ical.database.DAOFactory;
import ical.database.dao.LessonRemainingTimeDAO;
import ical.database.dao.ProfessorDAO;
import ical.database.entity.Lesson;
import ical.database.entity.LessonRemainingTime;
import ical.database.entity.Professor;
import ical.manager.ScheduleManager;
import ical.util.Config;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * NextLessonCommand class.
 *
 * @author Benoît Martel
 * @version 1.1
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

            if(ctx.getArgs().size() == 0) {

                ArrayList<Lesson> lessons = scheduleManager.getSchedule(ctx.getGuild().getId()).getNextLessons();

                if (lessons.size() == 1) {

                    channel.sendMessage(makeMessageEmbed(lessons.get(0))).queue(message -> {
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
                    channel
                            .sendMessage("Pas de prochain cours")
                            .queue((message -> message.delete().queueAfter(5, TimeUnit.SECONDS)));
                }
            }
            else if(ctx.getArgs().size() == 1){

                ProfessorDAO professorDAO = (ProfessorDAO) DAOFactory.getProfessorDAO();
                Professor professor = professorDAO.getProfessorFromParam(ctx.getArgs().get(0));

                if(professor != null){
                    Lesson lesson = scheduleManager.getSchedule(ctx.getGuild().getId()).getNextLesson(professor);
                    if(lesson != null){

                        channel.sendMessage(makeMessageEmbed(lesson)).queue(message -> {
                            LessonRemainingTime lessonRemainingTime =
                                    new LessonRemainingTime(lesson, message.getIdLong(), message.getChannel().getIdLong());
                            LessonRemainingTimeDAO lessonRemainingTimeDAO =
                                    (LessonRemainingTimeDAO) DAOFactory.getLesson_Remaining_Time();
                            lessonRemainingTimeDAO.create(lessonRemainingTime);
                        });
                    }
                    else{
                        ctx.getChannel()
                                .sendMessage("Pas de prochain cours prévu avec "+professor.getName())
                                .queue((message -> message.delete().queueAfter(5, TimeUnit.SECONDS)));
                    }
                }
                else{
                    ctx.getChannel()
                            .sendMessage("Impossible de trouver un professeur avec ton paramètre, ré-essaye !")
                            .queue((message -> message.delete().queueAfter(5, TimeUnit.SECONDS)));
                }

            }else{
                ctx.getChannel()
                        .sendMessage("Commande invalide ! Consulte l'aide.")
                        .queue((message -> message.delete().queueAfter(5, TimeUnit.SECONDS)));
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
                "Utilisation : `"+ Config.get("prefix")+getName()+" [{keyword/id professor}]`";
    }

    private MessageEmbed makeMessageEmbed(Lesson lesson){
        final EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("  " + lesson.getName() + "  dans " + lesson.timeRemaining(), null);
        eb.setColor(new Color(238358));
        eb.setThumbnail(lesson.getProfessor().getUrl());
        eb.addField("Date", lesson.getDay(), false);
        eb.addField("Début du cours", lesson.getStartTime(), true);
        eb.addField("Fin du cours", lesson.getEndTime(), true);
        eb.addField("Salle", lesson.getRoom(), false);
        eb.addField("Plus d'infos", lesson.getDescription(), false);

        return eb.build();
    }

}

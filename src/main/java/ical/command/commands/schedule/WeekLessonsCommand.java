package ical.command.commands.schedule;

import ical.command.GuildCommandContext;
import ical.command.commands.AbstractScheduleCommand;
import ical.database.entity.Lesson;
import ical.graphic.Timetable;
import ical.manager.ScheduleManager;
import ical.util.Config;
import net.dv8tion.jda.api.EmbedBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * LessonWeekCommand class.
 *
 * @author Benoît Martel
 * @version 1.0
 * @since 1.7
 */
public class WeekLessonsCommand extends AbstractScheduleCommand {

    /**
     * the logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(WeekLessonsCommand.class);

    /**
     * Default constructor.
     *
     * @param scheduleManager the schedule manager
     */
    public WeekLessonsCommand(ScheduleManager scheduleManager) {
        super(scheduleManager);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handle(GuildCommandContext ctx) {

            if (ctx.getArgs().size() == 0) {

                final ArrayList<Lesson> lessons = scheduleManager.getSchedule(ctx.getGuild().getId()).getWeekLessons();
                if (lessons.size() > 0) {
                    try {
                        Timetable timetable = new Timetable(lessons);
                        InputStream inputStream = timetable.generateTimetable();
                        String nameImg = "WEEK_" + UUID.randomUUID() + ".png";
                        if (inputStream != null) {
                            final EmbedBuilder eb = new EmbedBuilder();
                            eb.setTitle("Les cours pr\u00e9vus cette semaine  ", null);
                            eb.setColor(new Color(238358));
                            eb.setImage("attachment://" + nameImg);
                            eb.setTimestamp(scheduleManager.getSchedule(ctx.getGuild().getId()).getCreationDate());
                            eb.setFooter(
                                    "ENT",
                                    "https://www-ensibs.univ-ubs.fr/skins/ENSIBS/resources/img/logo.png"
                            );

                            ctx.getChannel().sendMessage(eb.build()).addFile(inputStream, nameImg).queue();
                        }
                    } catch (IOException exception) {
                        LOGGER.error(exception.getMessage(), exception.fillInStackTrace());
                        ctx.getChannel()
                                .sendMessage("Erreur lors de la génération du planning de la semaine")
                                .queue((message -> message.delete().queueAfter(5, TimeUnit.SECONDS)));
                    }


                } else
                    ctx.getChannel()
                            .sendMessage("Pas de cours prévu cette semaine, repose toi !")
                            .queue((message -> message.delete().queueAfter(5, TimeUnit.SECONDS)));
            } else if (ctx.getArgs().size() == 1) {
                if (ctx.getArgs().get(0).equalsIgnoreCase("-next")) {

                    final ArrayList<Lesson> lessons =
                            scheduleManager.getSchedule(ctx.getGuild().getId()).getNextWeekLessons();

                    if (lessons.size() > 0) {
                        try {
                            Timetable timetable = new Timetable(lessons);
                            InputStream inputStream = timetable.generateTimetable();
                            String nameImg = "WEEK_" + UUID.randomUUID() + ".png";
                            if (inputStream != null) {
                                final EmbedBuilder eb = new EmbedBuilder();
                                eb.setTitle("Les cours pr\u00e9vus la semaine prochaine ", null);
                                eb.setColor(new Color(238358));
                                eb.setImage("attachment://" + nameImg);
                                eb.setTimestamp(scheduleManager.getSchedule(ctx.getGuild().getId()).getCreationDate());
                                eb.setFooter(
                                        "ENT",
                                        "https://www-ensibs.univ-ubs.fr/skins/ENSIBS/resources/img/logo.png"
                                );

                                ctx.getChannel().sendMessage(eb.build()).addFile(inputStream, nameImg).queue();
                            } else
                                ctx.getChannel()
                                        .sendMessage("Erreur lors de la génération du planning, ré-essaye plus tard.")
                                        .queue((message -> message.delete().queueAfter(5, TimeUnit.SECONDS)));

                        } catch (IOException exception) {
                            LOGGER.error(exception.getMessage(), exception.fillInStackTrace());
                            ctx.getChannel()
                                    .sendMessage("Erreur lors de la génération du planning de la semaine prochaine")
                                    .queue((message -> message.delete().queueAfter(5, TimeUnit.SECONDS)));
                        }

                    } else
                        ctx.getChannel().sendMessage("Pas de cours prévu la semaine prochaine, repose toi !").queue();
                } else if(ctx.getArgs().get(0).matches("\\d+") && Integer.parseInt(ctx.getArgs().get(0)) > 0 && Integer.parseInt(ctx.getArgs().get(0)) <= 52 ){
                    final ArrayList<Lesson> lessons = scheduleManager.getSchedule(ctx.getGuild().getId()).getWeekLessons(Integer.parseInt(ctx.getArgs().get(0)));
                    if (lessons.size() > 0) {
                        try {
                            Timetable timetable = new Timetable(lessons);
                            InputStream inputStream = timetable.generateTimetable();
                            String nameImg = "WEEK_" + UUID.randomUUID() + ".png";
                            if (inputStream != null) {
                                final EmbedBuilder eb = new EmbedBuilder();
                                eb.setTitle("Les cours pr\u00e9vus la semaine n°"+ctx.getArgs().get(0), null);
                                eb.setColor(new Color(238358));
                                eb.setImage("attachment://" + nameImg);
                                eb.setTimestamp(scheduleManager.getSchedule(ctx.getGuild().getId()).getCreationDate());
                                eb.setFooter(
                                        "ENT",
                                        "https://www-ensibs.univ-ubs.fr/skins/ENSIBS/resources/img/logo.png"
                                );

                                ctx.getChannel().sendMessage(eb.build()).addFile(inputStream, nameImg).queue();
                            }
                        } catch (IOException exception) {
                            LOGGER.error(exception.getMessage(), exception.fillInStackTrace());
                            ctx.getChannel()
                                    .sendMessage("Erreur lors de la génération du planning de la semaine")
                                    .queue((message -> message.delete().queueAfter(5, TimeUnit.SECONDS)));
                        }


                    } else
                        ctx.getChannel()
                                .sendMessage("Pas de cours prévu la semaine n°"+ctx.getArgs().get(0)+", repose toi !")
                                .queue((message -> message.delete().queueAfter(5, TimeUnit.SECONDS)));
                } else
                    ctx.getChannel()
                            .sendMessage("Option invalide ! Consulte l'aide.")
                            .queue((message -> message.delete().queueAfter(5, TimeUnit.SECONDS)));
            } else
                ctx.getChannel()
                        .sendMessage("Commande invalide ! Consulte l'aide.")
                        .queue((message -> message.delete().queueAfter(5, TimeUnit.SECONDS)));

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "weekLessons";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getHelp() {
        return "Affiche les cours qui auront lieu dans la semaine\n"
                + "Utilisation :\n\t `"
                + Config.get("prefix")
                + getName()
                +" [-next]` : affiche les cours de la semaine prochaine";
    }

}

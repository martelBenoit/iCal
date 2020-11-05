package ical.command.commands.schedule;

import ical.command.GuildCommandContext;
import ical.command.commands.AbstractScheduleCommand;
import ical.manager.ScheduleManager;
import ical.database.entity.Lesson;
import ical.util.Config;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * LessonCommand class.
 *
 * @author Benoît Martel
 * @version 1.0
 * @since 1.0
 */
public class LessonsCommand extends AbstractScheduleCommand {

    /**
     * Default constructor.
     *
     * @param scheduleManager the schedule manager
     */
    public LessonsCommand(ScheduleManager scheduleManager) {
        super(scheduleManager);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handle(GuildCommandContext ctx) {

            if (ctx.getArgs().size() == 1) {
                int day;
                try{
                    day = Integer.parseInt(ctx.getArgs().get(0));
                }catch (NumberFormatException e){
                    ctx.getChannel()
                            .sendMessage("C'est un entier que j'attends en paramètre, ré-essaye \uD83D\uDE09")
                            .queue((message -> message.delete().queueAfter(5, TimeUnit.SECONDS)));
                    return;
                }

                final ArrayList<Lesson> lessons = scheduleManager.getSchedule(ctx.getGuild().getId()).getLessons(day);
                if (lessons.size() > 0) {
                    final EmbedBuilder eb = new EmbedBuilder();
                    eb.setTitle("Les cours pr\u00e9vus le  " + lessons.get(0).getDay(), null);
                    eb.setColor(new Color(238358));
                    for (final Lesson lesson : lessons) {
                        eb.addField(lesson.getName() + "\n*D\u00e9but du cours*", lesson.getStartTime(), true);
                        eb.addField("\u200b\n*Fin du cours*", lesson.getEndTime(), true);
                        eb.addBlankField(false);
                    }
                    ctx.getChannel().sendMessage(eb.build()).queue();
                } else
                    ctx.getChannel()
                            .sendMessage("Pas de cours, repose toi !")
                            .queue((message -> message.delete().queueAfter(5, TimeUnit.SECONDS)));
            }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "lessons";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getHelp() {
        return "Affiche les cours qui auront lieu dans {nombre} jour(s) par rapport à aujourd'hui.\n" +
                "Utilisation : `"+ Config.get("prefix")+getName()+" {nombre}`";
    }

}

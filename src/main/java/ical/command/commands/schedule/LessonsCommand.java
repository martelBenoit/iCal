package ical.command.commands.schedule;

import ical.command.CommandContext;
import ical.command.commands.AbstractScheduleCommand;
import ical.manager.ScheduleManager;
import ical.database.entity.Lesson;
import ical.util.Config;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.util.ArrayList;

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
    public void handle(CommandContext ctx) {

        if(ctx.getArgs().size() == 1){
            int day = Integer.parseInt(ctx.getArgs().get(0));

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
                ctx.getChannel().sendMessage("Pas de cours, repose toi !").queue();
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

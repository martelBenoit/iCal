package ical.command.commands.schedule;

import ical.command.GuildCommandContext;
import ical.command.commands.AbstractScheduleCommand;
import ical.database.entity.Lesson;
import ical.manager.ScheduleManager;
import ical.util.Config;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * TodayLessonsCommand class.
 *
 * @author Benoît Martel
 * @version 1.0
 * @since 1.0
 */
public class TodayLessonsCommand extends AbstractScheduleCommand {

    /**
     * Default constructor.
     *
     * @param scheduleManager the schedule manager
     */
    public TodayLessonsCommand(ScheduleManager scheduleManager) {
        super(scheduleManager);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handle(GuildCommandContext ctx) {

            final ArrayList<Lesson> lessons = scheduleManager.getSchedule(ctx.getGuild().getId()).getLessons(0);

            if (lessons.size() > 0) {
                final EmbedBuilder eb = new EmbedBuilder();
                eb.setTitle("Les cours pr\u00e9vus dans la journ\u00e9e : ", null);
                eb.setColor(new Color(238358));
                for (final Lesson lesson : lessons) {
                    eb.addField(lesson.getName() + "\n*D\u00e9but du cours*", lesson.getStartTime(), true);
                    eb.addField("\u200b\n*Fin du cours*", lesson.getEndTime(), true);
                    eb.addBlankField(false);
                }
                ctx.getChannel().sendMessage(eb.build()).queue();
            } else {
                ctx.getChannel()
                        .sendMessage("Pas de cours aujourd'hui, repose toi !")
                        .queue((message -> message.delete().queueAfter(5, TimeUnit.SECONDS)));
            }
        }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "todayLessons";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getHelp() {
        return "Indique les cours qui se déroule aujourd'hui.\n"+
                "Utilisation : `"+ Config.get("prefix")+getName()+"`";
    }

}

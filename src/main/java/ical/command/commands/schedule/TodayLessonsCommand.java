package ical.command.commands.schedule;

import ical.command.CommandContext;
import ical.command.commands.AbstractScheduleCommand;
import ical.database.entity.Lesson;
import ical.manager.ScheduleManager;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.util.ArrayList;

public class TodayLessonsCommand extends AbstractScheduleCommand {

    public TodayLessonsCommand(ScheduleManager scheduleManager) {
        super(scheduleManager);
    }

    @Override
    public void handle(CommandContext ctx) {

        final ArrayList<Lesson> lessons = scheduleManager.getSchedule(ctx.getGuild().getId()).getLessons(0);

        //scheduleManager.getSchedule(ctx.getGuild().getId()).getNextLessons().get(0).setDate();
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
        }
        else {
            ctx.getChannel().sendMessage("Pas de cours aujourd'hui, repose toi !").queue();
        }
    }

    @Override
    public String getName() {
        return "todayLessons";
    }

    @Override
    public String getHelp() {
        return "Indique les cours qui se déroule aujourd'hui";
    }

}

package ical.manager;

import ical.command.GuildCommandContext;
import ical.command.IGuildCommand;
import ical.command.commands.*;
import ical.command.commands.reminder.ReminderCommand;
import ical.command.commands.schedule.*;
import ical.util.Config;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class GuildCommandManager extends CommandManager {

    public GuildCommandManager(ScheduleManager scheduleManager){
        addCommand(new HelpCommand(this));
        addCommand(new DefaultChannelCommand());
        addCommand(new URLScheduleCommand(scheduleManager));
        addCommand(new NextLessonCommand(scheduleManager));
        addCommand(new TodayLessonsCommand(scheduleManager));
        addCommand(new TomorrowLessonsCommand(scheduleManager));
        addCommand(new LessonsCommand(scheduleManager));
        addCommand(new InfoCommand(scheduleManager));
        addCommand(new ModifNotifCommand());
        addCommand(new LessonNotifCommand());
        addCommand(new RoomCommand(scheduleManager));
        addCommand(new LessonWeekCommand(scheduleManager));
        addCommand(new ReminderCommand());

    }

    void handle(GuildMessageReceivedEvent event){

        String[] split = event.getMessage().getContentRaw()
                        .replaceFirst("(?i)" + Pattern.quote(Config.get("prefix")),"")
                        .split("\\s+");

        String invoke = split[0].toLowerCase();

        IGuildCommand cmd = (IGuildCommand) this.getCommand(invoke);

        if(cmd != null){
            event.getChannel().sendTyping().queue();
            List<String> args = Arrays.asList(split).subList(1,split.length);

            GuildCommandContext ctx = new GuildCommandContext(event, args);

            cmd.handle(ctx);
        }
    }
}


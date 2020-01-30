package ical.manager;

import ical.command.CommandContext;
import ical.command.ICommand;
import ical.command.commands.*;
import ical.command.commands.schedule.*;
import ical.util.Config;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class CommandManager {

    private final List<ICommand> commands = new ArrayList<>();

    public CommandManager(ScheduleManager scheduleManager){
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

    }

    private void addCommand(ICommand cmd){
        boolean nameFound = this.commands.stream().anyMatch(it -> it.getName().equalsIgnoreCase(cmd.getName()));

        if(nameFound){
            throw new IllegalArgumentException("A command with this name is already present");
        }

        commands.add(cmd);
    }

    public List<ICommand> getCommands(){
        return commands;
    }

    @Nullable
    public ICommand getCommand(String search){

        ICommand ret = null;

        for (ICommand command : this.commands) {
            if(command.getName().equalsIgnoreCase(search)){
                ret = command;
            }
        }
/*
        for (ICommand command : this.commands) {
            if(command.getName().equals(searchLower) || command.getAliases().contains(searchLower)){
                ret = command;
                break;
            }
        }*/

        return ret;

    }

    void handle(GuildMessageReceivedEvent event){

        String[] split = event.getMessage().getContentRaw()
                        .replaceFirst("(?i)" + Pattern.quote(Config.get("prefix")),"")
                        .split("\\s+");

        String invoke = split[0].toLowerCase();

        ICommand cmd = this.getCommand(invoke);

        if(cmd != null){
            event.getChannel().sendTyping().queue();
            List<String> args = Arrays.asList(split).subList(1,split.length);

            CommandContext ctx = new CommandContext(event, args);

            cmd.handle(ctx);
        }
    }
}


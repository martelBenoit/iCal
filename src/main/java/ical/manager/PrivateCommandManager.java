package ical.manager;

import ical.command.IPrivateCommand;
import ical.command.PrivateCommandContext;
import ical.command.commands.HelpCommand;
import ical.command.commands.reminder.ReminderCommand;
import ical.util.Config;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;


import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class PrivateCommandManager extends CommandManager{

    public PrivateCommandManager(){
        addCommand(new ReminderCommand());
        addCommand(new HelpCommand(this));

    }

    void handle(PrivateMessageReceivedEvent event){

        String[] split = event.getMessage().getContentRaw()
                .replaceFirst("(?i)" + Pattern.quote(Config.get("prefix")),"")
                .split("\\s+");

        String invoke = split[0].toLowerCase();

        IPrivateCommand cmd = (IPrivateCommand) this.getCommand(invoke);

        if(cmd != null){
            event.getChannel().sendTyping().queue();
            List<String> args = Arrays.asList(split).subList(1,split.length);

            PrivateCommandContext ctx = new PrivateCommandContext(event, args);

            cmd.handle(ctx);
        }
    }

}

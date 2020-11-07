package ical.manager;

import ical.command.IPrivateCommand;
import ical.command.PrivateCommandContext;
import ical.command.commands.tools.ClearCommand;
import ical.command.commands.HelpCommand;
import ical.command.commands.reminder.ReminderCommand;
import ical.command.commands.tools.InfoCommand;
import ical.command.commands.tools.SecretCommand;
import ical.util.Config;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;


import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * PrivateCommandManager class.
 *
 * @version 1.0
 * @since 1.8
 * @author Benoît Martel
 * @see CommandManager
 */
public class PrivateCommandManager extends CommandManager{

    /**
     * Default constructor.
     *
     * <br>Loads commands that can be used.
     *
     */
    public PrivateCommandManager(){
        addCommand(new ReminderCommand());
        addCommand(new HelpCommand(this));
        addCommand(new ClearCommand());
        addCommand(new InfoCommand());
        addCommand(new SecretCommand());

    }

    /**
     * The handle for private message received event.
     *
     * @param event the private message received event
     */
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

package ical.command.commands.tools;

import ical.command.IPrivateCommand;
import ical.command.PrivateCommandContext;
import ical.util.Config;
import net.dv8tion.jda.api.entities.Message;

import java.util.List;


public class ClearCommand implements IPrivateCommand {


    @Override
    public void handle(PrivateCommandContext ctx) {

        new Thread(() ->
        {
                List<Message> messages = ctx.getChannel().getIterableHistory().complete();
                messages.removeIf(m -> m.getAuthor().getIdLong() != ctx.getEvent().getJDA().getSelfUser().getIdLong());
                ctx.getChannel().purgeMessages(messages);

        })
                .run();

    }

    @Override
    public String getName() {
        return "clear";
    }

    @Override
    public String getHelp() {
        return "Permet de supprimer les messages d'ICal dans tes messages privées avec moi\n"+
                "Utilisation : `"+ Config.get("prefix")+getName()+"`";
    }
}

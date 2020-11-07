package ical.command.commands.tools;

import ical.command.IPrivateCommand;
import ical.command.PrivateCommandContext;
import ical.util.Config;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class SecretCommand implements IPrivateCommand {

    /**
     * the logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SecretCommand.class);

    @Override
    public void handle(PrivateCommandContext ctx) {
        if(ctx.getAuthor().getId().equals(Config.get("OWNER_ID"))){
            if(ctx.getArgs().size() == 3 && ctx.getArgs().get(0).equalsIgnoreCase("clear")){
                new Thread(() ->
                {
                    try{
                        TextChannel channel = ctx.getEvent().getJDA().getTextChannelById(ctx.getArgs().get(1));
                        if(channel != null){
                            List<Message> messages = channel.getIterableHistory().complete();
                            messages.removeIf(m -> m.getAuthor().getIdLong() != ctx.getEvent().getJDA().getSelfUser().getIdLong());
                            int nb = Integer.parseInt(ctx.getArgs().get(2));
                            if(nb < messages.size()) {
                                messages = messages.subList(0, nb);
                                channel.purgeMessages(messages);
                            }
                        }
                        else{
                            LOGGER.error("Invalid channel");
                        }


                    }catch(Exception e){
                        LOGGER.error(e.getMessage(),e.fillInStackTrace());
                    }


                }).start();
            }
        }
    }

    @Override
    public String getName() {
        return "secret";
    }

    @Override
    public String getHelp() {
        return null;
    }


}

package ical.core.runnable;

import ical.database.DAOFactory;
import ical.database.dao.ReminderDAO;
import ical.database.entity.Reminder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.exceptions.MissingAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @since 1.8
 */
public class CheckReminder implements Runnable {

    private JDA jda;

    /**
     * the logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(CheckReminder.class);

    private final ReminderDAO reminderDAO = (ReminderDAO) DAOFactory.getReminder();

    public CheckReminder(JDA jda) {
        this.jda = jda;
    }
    
    @Override
    public void run() {
        for(Reminder reminder : reminderDAO.findAll()){
            if(reminder.timeRemainingInSeconds() <= 0){
                try{
                    String message = "**Votre rappel :** "+reminder.getName();
                    if(reminder.getGuild() == null){
                        jda.openPrivateChannelById(reminder.getRecipient()).queue((privateChannel -> privateChannel.sendMessage(message).queue()));
                        if(!reminderDAO.delete(reminder))
                            LOGGER.error("Error when deleting reminder "+reminder.getId());
                    }
                    else{
                        Guild guild = jda.getGuildById(reminder.getGuild().getIdGuild());
                        if(guild != null){
                            TextChannel channel = guild.getTextChannelById(reminder.getRecipient());
                            if(channel != null) {
                                channel.sendMessage(message).queue();
                                if(!reminderDAO.delete(reminder))
                                    LOGGER.error("Error when deleting reminder "+reminder.getId());
                            }
                            else
                                LOGGER.error("Channel "+reminder.getRecipient()+ "doesn't exist");
                        }
                        else
                            LOGGER.error("Guild "+reminder.getGuild().getIdGuild()+ "doesn't exist");

                    }
                }catch (Exception e){
                    LOGGER.error(e.getMessage());
                    if(!reminderDAO.delete(reminder))
                        LOGGER.error("Error when deleting reminder "+reminder.getId());
                }
            }
        }
    }
}

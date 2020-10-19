package ical.manager;

import ical.core.Schedule;
import ical.core.runnable.*;
import ical.database.DAOFactory;
import ical.database.dao.GuildDAO;
import ical.database.entity.OGuild;
import ical.util.Config;

import me.duncte123.botcommons.BotCommons;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.ArrayList;

public class Listener extends ListenerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(Listener.class);

    private final ScheduleManager scheduleManager = new ScheduleManager();
    private final GuildCommandManager guildCommandManager = new GuildCommandManager(scheduleManager);
    private final PrivateCommandManager privateCommandManager = new PrivateCommandManager();
    private final TaskScheduler taskScheduler = new TaskScheduler();

    @Override
    public void onReady(@Nonnull ReadyEvent event) {

        taskScheduler.runMinutely("CheckSchedule", new CheckSchedule(event.getJDA(),scheduleManager));
        taskScheduler.runPeriod("UpdateSchedule",new UpdateSchedule(scheduleManager,event.getJDA()),5);
        taskScheduler.runAtMidnight("UpdateAvatar",new UpdateAvatar(event.getJDA()));
        taskScheduler.runMinutelySpecial(
                "UpdateLessonRemainingTimeMessage",
                new UpdateRemainingTimeLessonMessage(event.getJDA())
        );
        taskScheduler.runAt8H5MEveryMonday(
                "WeekInformationPlanning",
                new WeekInformationPlanning(event.getJDA(),scheduleManager)
        );
        taskScheduler.runMinutely("CheckReminder",new CheckReminder(event.getJDA()));

        //Run synchronously instead of async with waiter
        new UpdateAvatar(event.getJDA()).run();

        GuildDAO guildDAO = (GuildDAO) DAOFactory.getGuildDAO();
        ArrayList<OGuild> guilds = guildDAO.findAll();
        for(OGuild guild : guilds){
            scheduleManager.addSchedule(guild.getIdGuild(),new Schedule(guild.getUrlSchedule()));
        }

        LOGGER.info("Configuration done !");
        LOGGER.info("{} is ready", event.getJDA().getSelfUser().getAsTag());

    }



    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event){

        User user = event.getAuthor();

        if(user.isBot() || event.isWebhookMessage()){
            return;
        }

        String prefix = Config.get("prefix");
        String raw = event.getMessage().getContentRaw();

        if(raw.equalsIgnoreCase(prefix + "shutdown")
                && event.getAuthor().getId().equals(Config.get("owner_id"))){
            LOGGER.info("Shutting down");
            event.getJDA().shutdown();
            BotCommons.shutdown(event.getJDA());

            return;
        }

        if(raw.startsWith(prefix)){
            guildCommandManager.handle(event);
        }
    }

    @Override
    public void onPrivateMessageReceived(@Nonnull PrivateMessageReceivedEvent event){

        User user = event.getAuthor();

        if(user.isBot()){
            return;
        }

        String prefix = Config.get("prefix");
        String raw = event.getMessage().getContentRaw();

        if(raw.equalsIgnoreCase(prefix + "shutdown")
                && event.getAuthor().getId().equals(Config.get("owner_id"))){
            LOGGER.info("Shutting down");
            event.getJDA().shutdown();
            BotCommons.shutdown(event.getJDA());

            return;
        }

        if(raw.startsWith(prefix)){
            privateCommandManager.handle(event);
        }
    }

    @Override
    public void onGuildJoin(@Nonnull GuildJoinEvent event) {

        GuildDAO guildDAO = (GuildDAO) DAOFactory.getGuildDAO();
        ArrayList<OGuild> guilds = guildDAO.findAll();

        OGuild guild = new OGuild(event.getGuild().getId());


        if(!guilds.contains(guild)){
            if(guildDAO.create(guild) != null){
                if(scheduleManager.addSchedule(guild.getIdGuild(),new Schedule(null)))
                    LOGGER.info("Bot joined a new guild : "
                            + event.getGuild().getName()
                            + ", id = "+event.getGuild().getId()
                    );
                else
                    LOGGER.error("Bot joined a new guild "
                            + "but the schedule object was not added to the schedule manager : "
                            + event.getGuild().getName()
                    );
            }
            else
                LOGGER.error("Bot joined a new guild without registered it : "
                        + event.getGuild().getName()
                        + ", id = "+event.getGuild().getId()
                );
        }

    }

    @Override
    public void onGuildLeave(@Nonnull GuildLeaveEvent event) {

        OGuild guild = new OGuild(event.getGuild().getId());


        GuildDAO guildDAO = (GuildDAO) DAOFactory.getGuildDAO();
        boolean resDB = guildDAO.delete(guild);
        if(scheduleManager.removeSchedule(guild.getIdGuild()))
            LOGGER.info("Schedule remove from the schedule manager");
        else
            LOGGER.error("Schedule not remove from the schedule manager");

        if(resDB){
            LOGGER.info("Bot left a guild : "+event.getGuild().getName()+", id = "+event.getGuild().getId());
        }
        else
            LOGGER.error("Bot left a guild without unregistered it : "
                    + event.getGuild().getName()
                    + ", id = "+event.getGuild().getId()
            );

    }

}
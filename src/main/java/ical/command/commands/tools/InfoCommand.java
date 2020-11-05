package ical.command.commands.tools;

import ical.command.GuildCommandContext;
import ical.command.IPrivateCommand;
import ical.command.PrivateCommandContext;
import ical.command.commands.AbstractScheduleCommand;
import ical.database.DAOFactory;
import ical.database.dao.GuildDAO;
import ical.database.entity.OGuild;
import ical.manager.ScheduleManager;
import ical.manager.TaskScheduler;
import ical.util.Config;

import java.util.concurrent.TimeUnit;

/**
 * InfoCommand class.
 *
 * @author Benoît Martel
 * @version 1.0
 * @since 1.4
 */
public class InfoCommand extends AbstractScheduleCommand implements IPrivateCommand {

    /**
     * Default constructor.
     *
     * @param scheduleManager the schedule manager
     */
    public InfoCommand(ScheduleManager scheduleManager) {
        super(scheduleManager);
    }

    /**
     * Constructor for private command.
     */
    public InfoCommand(){
        super(null);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handle(GuildCommandContext ctx) {

        if(scheduleManager == null){
            ctx.getChannel()
                    .sendMessage("Erreur interne, veuillez contacter mon propriétaire")
                    .queue((message -> message.delete().queueAfter(5, TimeUnit.SECONDS)));
            return;
        }

        if (ctx.getArgs().isEmpty() && ctx.getEvent().getGuild().getOwnerId().equals(ctx.getEvent().getAuthor().getId())) {
            String idGuild = ctx.getGuild().getId();
            GuildDAO guildDAO = (GuildDAO) DAOFactory.getGuildDAO();
            OGuild guild = guildDAO.find(idGuild);

            StringBuilder message = new StringBuilder();
            message.append("**Information sur l'état actuel de ma configuration sur le serveur **\n\n");

            if (guild != null) {
                String urlSchedule = guild.getUrlSchedule();
                String idDefaultChannel = guild.getIdChannel();

                message.append("Planning :\n");

                if (urlSchedule != null && !urlSchedule.equals(""))
                    message.append("✅ Lien du planning OK").append("\n");
                else
                    message.append("❌ Lien du planning pas configuré").append("\n");


                message.append("\nNotifications :\n");

                if (idDefaultChannel != null && !idDefaultChannel.equals("")) {

                    message.append("✅ Salon des notifications : <#").append(idDefaultChannel).append(">\n");
                } else
                    message.append("❌ Salon des notifications pas configuré").append("\n");


                if (guild.lessonNotifisEnabled())
                    message.append("✅");
                else
                    message.append("❎");
                message.append(" Notfication prochain cours\n");

                if (guild.modifNotifisEnabled())
                    message.append("✅");
                else
                    message.append("❎");
                message.append(" Notfication modification de cours\n");

                message.append("\nPersonnalisation :\n");

                if (guild.usingSpecificPPGranted())
                    message.append("✅ Photo de profil personnalisée des professeurs").append("\n");
                else
                    message.append("❎ Photo de profil personnalisée des professeurs").append("\n");

                ctx.getChannel()
                        .sendMessage(message).queue();
            }
        }

        else
            ctx.getChannel()
                    .sendMessage("❌ Petit coquin tu n'es pas autorisé à exécuter cette commande")
                    .queue((message -> message.delete().queueAfter(5, TimeUnit.SECONDS)));

    }

    @Override
    public void handle(PrivateCommandContext ctx) {
        if(ctx.getArgs().size() == 1 && ctx.getArgs().get(0).equals("+") && Config.get("owner_id").equals(ctx.getAuthor().getId())){
            ctx.getChannel().sendMessage(TaskScheduler.info()).queue();
        }
        else{
            ctx.getChannel().sendMessage("Mauvaise utilisation de la commande `"+Config.get("prefix")+getName()+"`").queue((message -> message.delete().queueAfter(5, TimeUnit.SECONDS)));
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "info";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getHelp() {
        return "Affiche l'état actuel de la configuration du bot sur le serveur.\n"+
                "Utilisation : `"+ Config.get("prefix")+getName()+"`";
    }


}

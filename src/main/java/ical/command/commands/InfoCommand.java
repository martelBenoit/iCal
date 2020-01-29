package ical.command.commands;

import ical.command.CommandContext;
import ical.database.DAOFactory;
import ical.database.dao.GuildDAO;
import ical.database.entity.OGuild;
import ical.manager.ScheduleManager;

public class InfoCommand extends AbtractScheduleCommand {

    public InfoCommand(ScheduleManager scheduleManager) {
        super(scheduleManager);
    }

    @Override
    public void handle(CommandContext ctx) {

        String idGuild = ctx.getGuild().getId();
        GuildDAO guildDAO = (GuildDAO) DAOFactory.getGuildDAO();
        OGuild guild = guildDAO.find(idGuild);

        StringBuilder message = new StringBuilder();
        message.append("**Information sur l'état actuel de ma configuration sur le serveur ** \n\n");

        if(guild != null){
            String urlSchedule = guild.getUrlSchedule();
            String idDefaultChannel = guild.getIdChannel();

            message.append("Planning :\n");

            if(urlSchedule != null && !urlSchedule.equals(""))
                message.append("✅ Lien du planning OK").append("\n");
            else
                message.append("❌ Lien du planning pas configuré").append("\n");


            message.append("\nNotifications :\n");

            if(idDefaultChannel != null && !idDefaultChannel.equals("")){

                message.append("✅ Salon des notifications : <#").append(idDefaultChannel).append(">\n");
            }
            else
                message.append("❌ Salon des notifications pas configuré").append("\n");


            if(guild.lessonNotifisEnabled())
                message.append("✅");
            else
                message.append("❎");
            message.append(" Notfication prochain cours\n");

            if(guild.modifNotifisEnabled())
                message.append("✅");
            else
                message.append("❎");
            message.append(" Notfication modification de cours");

            ctx.getChannel().sendMessage(message).queue();
        }

    }

    @Override
    public String getName() {
        return "info";
    }

    @Override
    public String getHelp() {
        return "Affiche l'état actuel de la configuration du bot sur le serveur";
    }
}

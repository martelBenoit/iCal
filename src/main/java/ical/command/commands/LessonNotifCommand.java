package ical.command.commands;

import ical.command.CommandContext;
import ical.command.ICommand;
import ical.database.DAOFactory;
import ical.database.dao.GuildDAO;
import ical.database.entity.OGuild;

public class LessonNotifCommand implements ICommand {

    @Override
    public void handle(CommandContext ctx) {

        if(ctx.getEvent().getGuild().getOwnerId().equals(ctx.getEvent().getAuthor().getId()))
            if(ctx.getArgs().size() == 1){
                String enable = ctx.getArgs().get(0);

                GuildDAO guildDAO = (GuildDAO) DAOFactory.getGuildDAO();
                OGuild guild = guildDAO.find(ctx.getGuild().getId());

                if(guild != null) {

                    if (enable.equalsIgnoreCase("true")) {

                        guild.setLessonNotif(true);
                        if (guildDAO.update(guild))
                            ctx.getChannel().sendMessage("✅ Notification des cours activée !").queue();
                        else
                            ctx.getChannel().sendMessage("❌ Erreur lors de la prise en compte de votre demande..").queue();

                    } else if (enable.equalsIgnoreCase("false")) {

                        guild.setLessonNotif(false);
                        if (guildDAO.update(guild))
                            ctx.getChannel().sendMessage("✅ Notification des cours désactivée !").queue();
                        else
                            ctx.getChannel().sendMessage("❌ Erreur lors de la prise en compte de votre demande..").queue();
                    } else
                        ctx.getChannel().sendMessage("❌ Paramètre de la commande incorrect !").queue();
                }
            }
            else
                ctx.getChannel().sendMessage("❌ Paramètre de la commande incorrect !").queue();
        else
            ctx.getChannel().sendMessage("❌ Petit coquin tu n'es pas autorisé à exécuter cette commande").queue();


    }

    @Override
    public String getName() {
        return "lessonNotif";
    }

    @Override
    public String getHelp() {
        return "Permet d'activer/désactiver les notifications pour le prochain cours qui arrive\n" +
                "Utilisation : `"+getName()+" [true/false]`";
    }
}
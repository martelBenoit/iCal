package ical.command.commands;

import ical.command.CommandContext;
import ical.command.ICommand;
import ical.database.DAOFactory;
import ical.database.dao.GuildDAO;
import ical.database.entity.OGuild;
import ical.util.Config;

/**
 * LessonNotif class.
 * <br> Implement ICommand.
 * <br> Manages the command which enables or disables notifications of lesson
 *
 * @author Benoît Martel
 * @version 1.0
 */
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

    /**
     * Get the name of lessonNotif command.
     *
     * @return "lessonNotif"
     */
    @Override
    public String getName() {
        return "lessonNotif";
    }

    /**
     * Get the help of the lessonNotif command.
     *
     * @return the help of the lessonNotif command
     */
    @Override
    public String getHelp() {
        return "Permet d'activer/désactiver les notifications pour le prochain cours qui arrive\n" +
                "Utilisation : `"+ Config.get("prefix")+getName()+" {true/false}`";
    }
}
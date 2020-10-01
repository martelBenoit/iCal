package ical.command.commands;

import ical.command.GuildCommandContext;
import ical.command.IGuildCommand;
import ical.database.DAOFactory;
import ical.database.dao.GuildDAO;
import ical.database.entity.OGuild;
import ical.util.Config;

/**
 * ModifNotif class.
 * <br> Implement ICommand.
 * <br> Manages the command which enables or disables notifications of schedule
 *
 * @author Benoît Martel
 * @version 1.0
 */
public class ModifNotifCommand implements IGuildCommand {

    /**
     * Used to process the modifNotif command.
     *
     * @param ctx the command context
     */
    @Override
    public void handle(GuildCommandContext ctx) {

            if (ctx.getEvent().getGuild().getOwnerId().equals(ctx.getEvent().getAuthor().getId()))
                if (ctx.getArgs().size() == 1) {
                    String enable = ctx.getArgs().get(0);

                    GuildDAO guildDAO = (GuildDAO) DAOFactory.getGuildDAO();
                    OGuild guild = guildDAO.find(ctx.getGuild().getId());

                    if (guild != null) {
                        if (enable.equalsIgnoreCase("true")) {

                            guild.setModifNotif(true);
                            if (guildDAO.update(guild))
                                ctx.getChannel().sendMessage("✅ Notification des modifications activée !").queue();
                            else
                                ctx.getChannel()
                                        .sendMessage("❌ Erreur lors de la prise en compte de votre demande..")
                                        .queue();

                        } else if (enable.equalsIgnoreCase("false")) {

                            guild.setModifNotif(false);
                            if (guildDAO.update(guild))
                                ctx.getChannel().sendMessage("✅ Notification des modifications désactivée !").queue();
                            else
                                ctx.getChannel()
                                        .sendMessage("❌ Erreur lors de la prise en compte de votre demande..")
                                        .queue();
                        } else
                            ctx.getChannel().sendMessage("❌ Paramètre de la commande incorrect !").queue();
                    }

                } else
                    ctx.getChannel().sendMessage("❌ Paramètre de la commande incorrect !").queue();
            else
                ctx.getChannel().sendMessage("❌ Petit coquin tu n'es pas autorisé à exécuter cette commande").queue();
        }


    /**
     * Get the name of modifNotif command.
     *
     * @return {@code modifNotif}
     */
    @Override
    public String getName() {
        return "modifNotif";
    }

    /**
     * Get the help of the modifNotif command.
     *
     * @return the help of the modifNotif command
     */
    @Override
    public String getHelp() {
        return "Permet d'activer/désactiver les notifications de modification de cours\n" +
                "Utilisation : `" +Config.get("prefix")+getName()+" {true/false}`";
    }
}

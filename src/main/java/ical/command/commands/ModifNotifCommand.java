package ical.command.commands;

import ical.command.CommandContext;
import ical.command.ICommand;
import ical.database.DAOFactory;
import ical.database.dao.GuildDAO;
import ical.database.entity.OGuild;

/**
 * ModifNotif class.
 * <br> Implement ICommand.
 * <br> Gère la commande qui permet d'activer ou désactiver les
 *
 * @author Benoît Martel
 * @version 1.0
 */
public class ModifNotifCommand implements ICommand {

    @Override
    public void handle(CommandContext ctx) {

        if(ctx.getEvent().getGuild().getOwnerId().equals(ctx.getEvent().getAuthor().getId()))
            if(ctx.getArgs().size() == 1){
                String enable = ctx.getArgs().get(0);

                if(enable.equalsIgnoreCase("true")){
                    GuildDAO guildDAO = (GuildDAO) DAOFactory.getGuildDAO();
                    OGuild guild = guildDAO.find(ctx.getGuild().getId());

                    guild.setModifNotif(true);
                    if(guildDAO.update(guild))
                        ctx.getChannel().sendMessage("✅ Notification des modifications activée !").queue();
                    else
                        ctx.getChannel().sendMessage("❌ Erreur lors de la prise en compte de votre demande..").queue();

                }
                else if(enable.equalsIgnoreCase("false")){
                    GuildDAO guildDAO = (GuildDAO) DAOFactory.getGuildDAO();
                    OGuild guild = guildDAO.find(ctx.getGuild().getId());

                    guild.setModifNotif(false);
                    if(guildDAO.update(guild))
                        ctx.getChannel().sendMessage("✅ Notification des modifications désactivée !").queue();
                    else
                        ctx.getChannel().sendMessage("❌ Erreur lors de la prise en compte de votre demande..").queue();
                }
                else
                    ctx.getChannel().sendMessage("❌ Paramètre de la commande incorrect !").queue();

            }
            else
                ctx.getChannel().sendMessage("❌ Paramètre de la commande incorrect !").queue();
        else
            ctx.getChannel().sendMessage("❌ Petit coquin tu es pas autorisé à exécuter cette commande").queue();
    }

    @Override
    public String getName() {
        return "modifNotif";
    }

    @Override
    public String getHelp() {
        return "Permet d'activer/désactiver les notifications de modification de cours\n" +
                "Utilisation : `"+getName()+" [true/false]`";
    }
}

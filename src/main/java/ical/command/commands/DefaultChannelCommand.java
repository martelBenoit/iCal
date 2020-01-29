package ical.command.commands;

import ical.command.CommandContext;
import ical.command.ICommand;
import ical.database.DAOFactory;
import ical.database.dao.GuildDAO;
import ical.database.entity.OGuild;

public class DefaultChannelCommand implements ICommand {

    @Override
    public void handle(CommandContext ctx) {

        if(ctx.getEvent().getGuild().getOwnerId().equals(ctx.getEvent().getAuthor().getId()))
            if(ctx.getArgs().isEmpty()){

                GuildDAO guildDAO = (GuildDAO) DAOFactory.getGuildDAO();
                OGuild guild = guildDAO.find(ctx.getGuild().getId());

                if(guild != null){
                    guild.setIdChannel(ctx.getChannel().getId());
                    if(guildDAO.update(guild))
                        ctx.getChannel().sendMessage("✅ Mise à jour du salon par défaut effectuée !").queue();
                    else
                        ctx.getChannel().sendMessage("❌ La prise en compte du nouveau salon par défaut n'a pas fonctionnée").queue();

                }
            }
        else
            ctx.getChannel().sendMessage("❌ Petit coquin tu es pas autorisé à exécuter cette commande").queue();

    }

    @Override
    public String getName() {
        return "setDefaultChannel";
    }

    @Override
    public String getHelp() {
        return "Permet de définir le salon sur lequelle les notifications du planning vont être affichées";
    }
}

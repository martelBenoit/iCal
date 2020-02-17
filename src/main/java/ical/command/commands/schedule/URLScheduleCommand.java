package ical.command.commands.schedule;

import ical.command.CommandContext;
import ical.command.commands.AbstractScheduleCommand;
import ical.manager.ScheduleManager;
import ical.database.DAOFactory;
import ical.database.dao.GuildDAO;
import ical.database.entity.OGuild;

import java.net.MalformedURLException;
import java.net.URL;

public class URLScheduleCommand extends AbstractScheduleCommand {

    public URLScheduleCommand(ScheduleManager manager) {
        super(manager);
    }

    @Override
    public void handle(CommandContext ctx) {

        if(ctx.getEvent().getGuild().getOwnerId().equals(ctx.getEvent().getAuthor().getId()))

            if(ctx.getArgs().size() == 1){
                String urlString = ctx.getArgs().get(0);

                try{
                    URL url = new URL(urlString);

                    GuildDAO guildDAO = (GuildDAO) DAOFactory.getGuildDAO();
                    OGuild guild = guildDAO.find(ctx.getGuild().getId());

                    if(guild != null){
                        guild.setUrlSchedule(urlString);
                        this.scheduleManager.getSchedule(ctx.getGuild().getId()).setURL(url);
                        if(guildDAO.update(guild))
                            ctx.getChannel().sendMessage("✅ Mise à jour du lien du planning réalisée !").queue();
                        else
                            ctx.getChannel().sendMessage("❌ Ca n'a pas fonctionné..").queue();

                    }

                }catch (MalformedURLException e){
                    ctx.getChannel().sendMessage("❌ Ton lien est pas valide !").queue();
                }
            }
            else
                ctx.getChannel().sendMessage("❌ Tu dois renseigner un lien avec cette commande").queue();
        else
            ctx.getChannel().sendMessage("❌ Petit coquin tu n'es pas autorisé à exécuter cette commande").queue();
    }

    @Override
    public String getName() {
        return "setUrlSchedule";
    }

    @Override
    public String getHelp() {
        return "Spécifier l'url du planning. Cet url doit renvoyer un fichier de type .ics\n" +
                "Utilisation : `"+getName()+" [url]` ";
    }
}

package ical.command.commands.schedule;

import ical.command.GuildCommandContext;
import ical.command.commands.AbstractScheduleCommand;
import ical.manager.ScheduleManager;
import ical.database.DAOFactory;
import ical.database.dao.GuildDAO;
import ical.database.entity.OGuild;
import ical.util.Config;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * URLScheduleCommand class.
 *
 * @author Benoît Martel
 * @version 1.0
 * @since 1.0
 */
public class URLScheduleCommand extends AbstractScheduleCommand {

    /**
     * Default constructor.
     *
     * @param manager the schedule manager
     */
    public URLScheduleCommand(ScheduleManager manager) {
        super(manager);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handle(GuildCommandContext ctx) {

            if (ctx.getEvent().getGuild().getOwnerId().equals(ctx.getEvent().getAuthor().getId()))

                if (ctx.getArgs().size() == 1) {
                    String urlString = ctx.getArgs().get(0);

                    try {
                        URL url = new URL(urlString);

                        GuildDAO guildDAO = (GuildDAO) DAOFactory.getGuildDAO();
                        OGuild guild = guildDAO.find(ctx.getGuild().getId());

                        if (guild != null) {
                            guild.setUrlSchedule(urlString);
                            this.scheduleManager.getSchedule(ctx.getGuild().getId()).setURL(url);
                            if (guildDAO.update(guild))
                                ctx.getChannel().sendMessage("✅ Mise à jour du lien du planning réalisée !").queue();
                            else
                                ctx.getChannel().sendMessage("❌ Ça n'a pas fonctionné..").queue();

                        }

                    } catch (MalformedURLException e) {
                        ctx.getChannel().sendMessage("❌ Ton lien n'est pas valide !").queue();
                    }
                } else
                    ctx.getChannel().sendMessage("❌ Tu dois renseigner un lien avec cette commande").queue();
            else
                ctx.getChannel().sendMessage("❌ Petit coquin tu n'es pas autorisé à exécuter cette commande").queue();
        }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "setUrlSchedule";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getHelp() {
        return "Spécifier l'url du planning. Cet url doit renvoyer un fichier de type .ics\n" +
                "Utilisation : `"+ Config.get("prefix")+getName()+" {url}` ";
    }

}

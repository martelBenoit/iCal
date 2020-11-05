package ical.command.commands.tools;

import ical.command.GuildCommandContext;

import ical.command.commands.AbstractScheduleCommand;
import ical.database.DAOFactory;
import ical.database.dao.GuildDAO;
import ical.database.entity.OGuild;
import ical.manager.ScheduleManager;
import ical.util.Config;

import java.util.concurrent.TimeUnit;

/**
 * PermissionCommand class.
 *
 * @author Benoît Martel
 * @version 1.0
 * @since 1.9
 */
public class PermissionCommand extends AbstractScheduleCommand {

    /**
     * Default constructor.
     *
     * @param scheduleManager the schedule manager
     */
    public PermissionCommand(ScheduleManager scheduleManager) {
        super(scheduleManager);
    }

    @Override
    public void handle(GuildCommandContext ctx) {

        if (ctx.getEvent().getGuild().getOwnerId().equals(ctx.getEvent().getAuthor().getId())) {
            if (ctx.getArgs().size() == 2) {
                if (ctx.getArgs().get(0).equalsIgnoreCase("pp")) {
                    String right = ctx.getArgs().get(1);

                    GuildDAO guildDAO = (GuildDAO) DAOFactory.getGuildDAO();
                    OGuild guild = guildDAO.find(ctx.getGuild().getId());

                    if (guild != null) {
                        if (right.equalsIgnoreCase("grant")) {

                            guild.setUsingSpecificPP(true);
                            if (guildDAO.update(guild)) {
                                scheduleManager.updatePP(guild.getIdGuild());
                                ctx.getChannel()
                                        .sendMessage("✅ Personnalisation et affichage des photos de profil des professeurs autorisées")
                                        .queue((message -> message.delete().queueAfter(5, TimeUnit.SECONDS)));

                            }else
                                ctx.getChannel()
                                        .sendMessage("❌ Erreur lors de la prise en compte de votre demande..")
                                        .queue((message -> message.delete().queueAfter(5, TimeUnit.SECONDS)));

                        } else if (right.equalsIgnoreCase("revoke")) {
                            guild.setUsingSpecificPP(false);
                            if (guildDAO.update(guild)) {
                                scheduleManager.updatePP(guild.getIdGuild());
                                ctx.getChannel()
                                        .sendMessage("✅ Personnalisation et affichage des photos de profil des professeurs révoquées")
                                        .queue((message -> message.delete().queueAfter(5, TimeUnit.SECONDS)));
                            }else
                                ctx.getChannel()
                                        .sendMessage("❌ Erreur lors de la prise en compte de votre demande..")
                                        .queue((message -> message.delete().queueAfter(5, TimeUnit.SECONDS)));
                        }
                        else{
                            ctx.getChannel()
                                    .sendMessage("❌ Paramètre de la commande incorrect !")
                                    .queue((message -> message.delete().queueAfter(5, TimeUnit.SECONDS)));
                        }
                    }else{
                        ctx.getChannel()
                                .sendMessage("❌ Paramètre de la commande incorrect !")
                                .queue((message -> message.delete().queueAfter(5, TimeUnit.SECONDS)));
                    }

                }else{
                    ctx.getChannel()
                            .sendMessage("❌ Paramètre de la commande incorrect !")
                            .queue((message -> message.delete().queueAfter(5, TimeUnit.SECONDS)));
                }
            }
        }else{
            ctx.getChannel()
                    .sendMessage("❌ Petit coquin tu n'es pas autorisé à exécuter cette commande")
                    .queue((message -> message.delete().queueAfter(5, TimeUnit.SECONDS)));
        }

    }

    @Override
    public String getName() {
        return "permission";
    }

    @Override
    public String getHelp() {
        return "Permet de gérer certaines permissions\n" +
                "Utilisation :" +
                "\n1. Choisir d'utiliser les PP personnalisées pour le serveur : `" + Config.get("prefix")+getName()+" pp {grant/revoke}`";
    }


}

package ical.command.commands.tools;

import ical.command.GuildCommandContext;
import ical.command.commands.AbstractScheduleCommand;
import ical.core.Schedule;
import ical.database.DAOFactory;
import ical.database.dao.GuildDAO;
import ical.database.dao.ProfessorDAO;
import ical.database.dao.Professor_Picture_By_GuildDAO;
import ical.database.entity.OGuild;
import ical.database.entity.Professor;
import ical.database.entity.Professor_Picture_By_Guild;
import ical.manager.ScheduleManager;
import ical.util.Config;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * ProfessorCommand class.
 *
 * @author Benoît Martel
 * @version 1.0
 * @since 1.9
 */
public class ProfessorCommand extends AbstractScheduleCommand {

    /**
     * the logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ProfessorCommand.class);

    /**
     * the professor DAO.
     */
    private final ProfessorDAO professorDAO = (ProfessorDAO) DAOFactory.getProfessorDAO();

    /**
     * the guild DAO.
     */
    private final GuildDAO guildDAO = (GuildDAO) DAOFactory.getGuildDAO();

    /**
     * the professor picture by guild DAO.
     */
    private final Professor_Picture_By_GuildDAO professorPictureByGuildDAO = (Professor_Picture_By_GuildDAO) DAOFactory.getProfessorPictureByGuild();


    /**
     * Default constructor.
     *
     * @param scheduleManager the schedule manager
     */
    public ProfessorCommand(ScheduleManager scheduleManager) {
        super(scheduleManager);
    }

    @Override
    public void handle(GuildCommandContext ctx) {

        // on récupère le serveur affecté par la commande
        OGuild guild = guildDAO.find(ctx.getGuild().getId());

        // on récupère l'auteur de la commande
        User author = ctx.getEvent().getAuthor();

        if (ctx.getArgs().size() == 2) {

            // Commande pour rechercher un professeur
            if (ctx.getArgs().get(0).equalsIgnoreCase("search")) {

                if (guild != null) {

                    // On récupère tous les professeurs dont le nom contient le mot-clé passé en 2nd paramètre de la commande
                    ArrayList<Professor> professors = professorDAO.searchByValue(ctx.getArgs().get(1));

                    // On filtre la liste pour ne garder que les professeurs intervenant dans les cours du serveur
                    professors = filterProfessor(professors, guild.getIdGuild());

                    if (!professors.isEmpty()) {
                        if (professors.size() == 1) {

                            Professor professor = professors.get(0);

                            if (guild.usingSpecificPPGranted()) {
                                Professor_Picture_By_Guild professorPictureByGuild = professorPictureByGuildDAO.findByGuildAndProfessor(guild, professor);
                                if (professorPictureByGuild != null)
                                    professor.setUrl(professorPictureByGuild.getUrl());
                            }

                            EmbedBuilder embedBuilder = new EmbedBuilder();
                            embedBuilder.setTitle("It's a match ! \uD83D\uDC96");
                            embedBuilder.setThumbnail(professor.getUrl());
                            embedBuilder.setColor(0x055B89);
                            embedBuilder.setFooter("ENT", "https://www-ensibs.univ-ubs.fr/skins/ENSIBS/resources/img/logo.png");
                            String description = "**" + professor.getDisplayName() + "**\n\n";
                            description += "Son identifiant : " + professor.getId();
                            description += "\nNombre de cours restants : " + scheduleManager.getSchedule(ctx.getGuild().getId()).getLessonWithProfessor(professor).size();
                            embedBuilder.appendDescription(description);
                            ctx.getChannel().sendMessage(embedBuilder.build()).queue();
                        } else {
                            StringBuilder message = new StringBuilder();
                            message.append("**Voici les résultats :**\n");
                            for (Professor professor : professors) {
                                message.append("  • ")
                                        .append(professor.getId()).append("\t")
                                        .append(professor.getDisplayName())
                                        .append("\n");
                            }
                            ctx.getChannel().sendMessage(message).queue();
                        }
                    } else
                        ctx.getChannel()
                                .sendMessage("Aucune corespondance trouvée \uD83D\uDE22")
                                .queue((message -> message.delete().queueAfter(5, TimeUnit.SECONDS)));

                } else {
                    ctx.getChannel()
                            .sendMessage("❌ Une erreur s'est produite, ré-essaye plus tard")
                            .queue((message -> message.delete().queueAfter(5, TimeUnit.SECONDS)));
                }


            } else {
                ctx.getChannel()
                        .sendMessage("❌ Erreur dans ta commande, consulte l'aide !")
                        .queue((message -> message.delete().queueAfter(5, TimeUnit.SECONDS)));
            }


        } else if (ctx.getArgs().size() == 3 && ctx.getArgs().get(0).equalsIgnoreCase("edit")) {

            int id;

            try {
                id = Integer.parseInt(ctx.getArgs().get(1));

            } catch (NumberFormatException e) {
                ctx.getChannel()
                        .sendMessage("❌ L'id que tu fournis n'est pas un nombre.\n> Psst je te donne un exemple : " + (int) (Math.random() * 1000))
                        .queue((message -> message.delete().queueAfter(5, TimeUnit.SECONDS)));
                return;
            }

            Professor professor = professorDAO.findById(id);

            if (professor != null && guild != null && checkProfessorIsUsed(professor, guild.getIdGuild())) {
                try {

                    EmbedBuilder eb = new EmbedBuilder();
                    eb.setTitle("Valides-tu ?");
                    eb.setDescription("Tu souhaites remplacer la photo de profil de " + professor.getDisplayName() + " par celle " +
                            "qui est actuellement affichée ?\n\uD83D\uDC4D : OUI\n\uD83D\uDC4E : NON\n\n>>> **PS 1** : Validation par le propriétaire du serveur uniquement \n**PS 2** : Le message s'auto-détruit dans deux minutes et tu ne pourras plus le valider\n**PS 3** : Si l'image sélectionnée ne s'affiche pas dans ce message alors, le lien est incorrect");
                    eb.setThumbnail(ctx.getArgs().get(2));
                    AtomicBoolean isChecked = new AtomicBoolean(false);

                    ctx.getChannel()
                            .sendMessage(eb.build()).queue(message -> new Thread(() -> {

                                message.delete().queueAfter(2, TimeUnit.MINUTES, (success) -> isChecked.set(true), (failure) -> isChecked.set(true));

                                String unicodeNo = "U+1f44e";
                                String unicodeYes = "U+1f44d";
                                message.addReaction(unicodeYes).queue();
                                message.addReaction(unicodeNo).queue();
                                Message m;

                                while (!isChecked.get()) {

                                    try {
                                        m = ctx.getChannel().retrieveMessageById(message.getId()).complete();
                                        if (m != null) {

                                            for (MessageReaction reaction : m.getReactions()) {
                                                // Récupération de la réaction YES
                                                if (reaction.getReactionEmote().getAsCodepoints().equals(unicodeYes)) {
                                                    if (reaction.retrieveUsers().stream().anyMatch(u -> u.getIdLong() == ctx.getGuild().getOwnerIdLong())) {


                                                        Professor_Picture_By_Guild professorPictureByGuild = new Professor_Picture_By_Guild(guild, professor, ctx.getArgs().get(2), author.getId());

                                                        if (professorPictureByGuildDAO.update(professorPictureByGuild)) {
                                                            scheduleManager.updatePP(guild.getIdGuild());
                                                            LOGGER.info("New profile picture for professor id " + professor.getId() + " : " + ctx.getArgs().get(2) + ", Author : " + author.getName());
                                                            ctx.getChannel().sendMessage("✅ La photo de profil de " + professor.getDisplayName() + " vient d'être mise à jour ! ")
                                                                    .queue((reply -> reply.delete().queueAfter(10, TimeUnit.SECONDS)));
                                                        } else {
                                                            LOGGER.error("Error for renew profile picture for professor id " + professor.getId() + " : " + ctx.getArgs().get(2) + ", Author : " + author.getName());
                                                            ctx.getChannel().sendMessage("❌ Une erreur s'est produite lors de la mise à jour de la photo de profil de  " + professor.getDisplayName())
                                                                    .queue((reply -> reply.delete().queueAfter(5, TimeUnit.SECONDS)));
                                                        }


                                                        m.delete().complete();
                                                        isChecked.set(true);
                                                    }
                                                }
                                                // Récupération de la réaction NO
                                                if (reaction.getReactionEmote().getAsCodepoints().equals(unicodeNo)) {
                                                    if (reaction.retrieveUsers().stream().anyMatch(u -> u.getIdLong() == ctx.getGuild().getOwnerIdLong())) {
                                                        ctx.getChannel().sendMessage("Changement de photo de profil pour " + professor.getDisplayName() + " annulé")
                                                                .queue((reply -> reply.delete().queueAfter(5, TimeUnit.SECONDS)));
                                                        m.delete().complete();
                                                        isChecked.set(true);
                                                    }
                                                }
                                            }

                                        }
                                    } catch (ErrorResponseException errorResponseException) {
                                        isChecked.set(true);
                                    } catch (Exception e) {
                                        LOGGER.error(e.getMessage(), e.fillInStackTrace());
                                        isChecked.set(true);
                                    }

                                }

                            }).start()
                    );
                } catch (IllegalArgumentException e) {
                    if (e.getMessage().equals("URL must be a valid http(s) or attachment url.")) {
                        ctx.getChannel()
                                .sendMessage("❌ L'URL renseignée est incorrecte ")
                                .queue((message -> message.delete().queueAfter(5, TimeUnit.SECONDS)));
                    } else {
                        ctx.getChannel()
                                .sendMessage("❌ Une erreur est apparue, vérifie l'URL !")
                                .queue((message -> message.delete().queueAfter(5, TimeUnit.SECONDS)));
                        LOGGER.error(e.getMessage(), e.fillInStackTrace());
                    }
                }
            } else {
                ctx.getChannel()
                        .sendMessage("❌ Je ne trouve aucun professeur avec cet l'id " + ctx.getArgs().get(1))
                        .queue((message -> message.delete().queueAfter(5, TimeUnit.SECONDS)));
            }
        }
        else {
            ctx.getChannel()
                    .sendMessage("❌ Erreur dans ta commande, consulte l'aide !")
                    .queue((message -> message.delete().queueAfter(5, TimeUnit.SECONDS)));
        }


    }

    @Override
    public String getName() {
        return "prof";
    }

    @Override
    public String getHelp() {
        return "Permet de gérer les professeurs\n" +
                "Utilisation :" +
                "\n\t• Rechercher un professeur       : `" + Config.get("prefix") + getName() + " search {keyword}`" +
                "\n\t• Modifier la PP d'un professeur : `" + Config.get("prefix") + getName() + " edit {id} {link}`";
    }

    /**
     * Filter the list to keep only the professors involved in the courses for a specific server.
     * <br>The list is also sorted alphabetically.
     *
     * @param professors professors list to filter
     * @param guildID    guild id
     * @return the list of filtered professors
     */
    private ArrayList<Professor> filterProfessor(ArrayList<Professor> professors, String guildID) {
        Schedule schedule = scheduleManager.getSchedule(guildID);
        professors.removeIf(p -> p.getDisplayName() == null);
        professors.removeIf(p -> p.getDisplayName().startsWith("CyberLog"));


        ArrayList<Professor> filteredList = (ArrayList<Professor>) professors.stream()
                .filter(p -> schedule.getLessons()
                        .stream()
                        .anyMatch(p2 -> p2.getProfessor().getDisplayName().equals(p.getDisplayName())))
                .collect(Collectors.toList());

        Collections.sort(filteredList);

        return filteredList;

    }

    /**
     * Used to find out whether the professor specified in the parameter intervenes in the lessons for the guild
     * specified in the second parameter.
     *
     * @param professor the professor
     * @param guildID   the guild id
     * @return true if the professor intervenes in the lessons for the guild
     */
    private boolean checkProfessorIsUsed(Professor professor, String guildID) {
        return (scheduleManager.getSchedule(guildID).getLessons().stream()
                .anyMatch(l -> l.getProfessor().getDisplayName().equals(professor.getDisplayName())));
    }

}

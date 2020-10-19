package ical.command.commands.reminder;

import ical.command.*;
import ical.database.DAOFactory;
import ical.database.dao.ReminderDAO;
import ical.database.entity.OGuild;
import ical.database.entity.Reminder;
import ical.util.Config;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ReminderCommand class.
 *
 * <br>Command available for guilds and for private messages.
 *
 * @author Benoît Martel
 * @version 1.0
 * @since 1.8
 * @see IGuildCommand
 * @see IPrivateCommand
 */
public class ReminderCommand implements IGuildCommand, IPrivateCommand {

    /**
     * the logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ReminderCommand.class);

    /**
     * the reminder DAO
     */
    private final ReminderDAO reminderDAO = (ReminderDAO) DAOFactory.getReminder();

    /**
     * {@inheritDoc}
     */
    @Override
    public void handle(PrivateCommandContext ctx) {

        if (ctx.getArgs().size() >= 3 && stringToDate(ctx.getArgs().get(0), ctx.getArgs().get(1)) != null) {
            Date date = stringToDate(ctx.getArgs().get(0), ctx.getArgs().get(1));

            if(date != null && date.before(new Date())){
                ctx.getChannel().sendMessage("Je ne pourrai pas te rappeler un évènement déjà passé.. \uD83E\uDD14").queue();
            }
            else{
                StringBuilder stringBuilder = new StringBuilder();
                for (String text : ctx.getArgs().subList(2, ctx.getArgs().size())) {
                    stringBuilder.append(text).append(" ");
                }
                String remindText;
                if(stringBuilder.length() > 0)
                    remindText = stringBuilder.substring(0,stringBuilder.length()-1);
                else
                    remindText = stringBuilder.toString();
                if(remindText.length() >= 250)
                    ctx.getChannel().sendMessage("Le texte de ton rappel est trop long, soit plus concis !").queue();

                else if (postReminder(ctx, remindText, date))
                    ctx.getChannel().sendMessage("Ton rappel personnel a correctement été défini ! \uD83D\uDE42").queue();
            }


        }
        else if(ctx.getArgs().size() >= 2 && ctx.getArgs().get(0).equalsIgnoreCase("-delete")){
            try{
                int id_reminder = Integer.parseInt(ctx.getArgs().get(1));
                Reminder reminder = reminderDAO.findById(id_reminder);

                if(reminder != null && reminder.getAuthor().equals(ctx.getAuthor().getId())){
                    boolean res = reminderDAO.delete(reminder);
                    if(res){
                        ctx.getChannel().sendMessage(reminder.getName()+" a bien été supprimé ! \uD83D\uDE42").queue();
                    }
                    else
                        ctx.getChannel().sendMessage("❌ Une erreur interne est apparu lors de la suppression de ton rappel..").queue();
                }
                else
                    ctx.getChannel().sendMessage("❌ Tu n'as pas de privilèges pour supprimer ce rappel (s'il existe)").queue();


            }catch (NumberFormatException exception){
                LOGGER.error(exception.getMessage());
                ctx.getChannel().sendMessage("❌ L'identifiant pour la supression du rappel n'est pas correct, j'attends un nombre").queue();
            }
        }

        else if(ctx.getArgs().size() >= 1 && ctx.getArgs().get(0).equalsIgnoreCase("-list")) {
            boolean printIdentifiant = false;

            if(ctx.getArgs().size() >= 2 && ctx.getArgs().get(1).equalsIgnoreCase("id"))
                printIdentifiant = true;

            ArrayList<Reminder> reminders = reminderDAO.findByRecipient(ctx.getAuthor());
            Collections.sort(reminders);

            if(reminders.size() > 0) {

                for(EmbedBuilder embedBuilder : constructList(reminders,true,printIdentifiant)){
                    ctx.getChannel().sendMessage(embedBuilder.build()).queue();

                }
            }
            else
                ctx.getChannel().sendMessage("Tu n'as pas de rappel").queue();

        }

        else if(ctx.getArgs().get(0).equalsIgnoreCase("-next")) {
            ArrayList<Reminder> reminders = reminderDAO.findByRecipient(ctx.getAuthor());
            Collections.sort(reminders);
            if(reminders.size() > 0) {
                Reminder reminder = reminders.get(0);

                String text = "Ton prochain rappel est : "+reminder.getName()+ "\n"
                        +"Prévu le "+ reminder.getDay() + " à " + reminder.getTime();

                ctx.getChannel().sendMessage(text).queue();
            }
            else
                ctx.getChannel().sendMessage("Tu n'as pas de rappel \uD83D\uDE42").queue();
        }
        else{
            ctx.getChannel().sendMessage("Tu sembles rencontrer des problèmes avec la commande.. Consulte l'aide ! \uD83D\uDE42").queue();
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handle(GuildCommandContext ctx) {


        MessageChannel channel = ctx.getChannel();

        if (ctx.getArgs().size() >= 4 && ctx.getArgs().get(0).startsWith("<#") && ctx.getArgs().get(0).endsWith(">") &&
                stringToDate(ctx.getArgs().get(1), ctx.getArgs().get(2)) != null) {

            Date date = stringToDate(ctx.getArgs().get(1), ctx.getArgs().get(2));
            if (date != null && date.before(new Date())) {
                ctx.getChannel().sendMessage("Je ne pourrai pas te rappeler un évènement déjà passé.. \uD83E\uDD14").queue();
            } else {
                String idRecipient = ctx.getArgs().get(0).substring(2, ctx.getArgs().get(0).length() - 1);

                GuildChannel channelRecipient = ctx.getGuild().getGuildChannelById(idRecipient);
                Member self = ctx.getGuild().getMember(ctx.getJDA().getSelfUser());
                if (channelRecipient != null && self != null) {
                    if (self.hasPermission(channelRecipient, Permission.MESSAGE_WRITE)) {
                        StringBuilder stringBuilder = new StringBuilder();
                        for (String text : ctx.getArgs().subList(3, ctx.getArgs().size())) {
                            stringBuilder.append(text).append(" ");
                        }
                        String remindText;
                        if (stringBuilder.length() > 0)
                            remindText = stringBuilder.substring(0, stringBuilder.length() - 1);
                        else
                            remindText = stringBuilder.toString();

                        if (remindText.length() >= 250)
                            channel.sendMessage("Le texte de ton rappel est trop long, soit plus concis ! \uD83D\uDE44").queue();
                        else if (postReminder(ctx, remindText, date, idRecipient))
                            channel.sendMessage("Le rappel a correctement été défini ! \uD83D\uDE42").queue();
                    } else {
                        channel.sendMessage("Je n'ai malheureusement pas le droit d'écrire dans <#" + idRecipient + "> \uD83D\uDE25").queue();
                    }
                } else {
                    channel.sendMessage("Je n'ai malheureusement pas le droit d'écrire dans <#" + idRecipient + "> \uD83D\uDE25").queue();
                }

            }


        } else if (ctx.getArgs().size() >= 3 && stringToDate(ctx.getArgs().get(0), ctx.getArgs().get(1)) != null) {
            Date date = stringToDate(ctx.getArgs().get(0), ctx.getArgs().get(1));

            StringBuilder remindText = new StringBuilder();
            for (String text : ctx.getArgs().subList(2, ctx.getArgs().size())) {
                remindText.append(text).append(" ");
            }

            if(remindText.toString().length() >= 250)
                channel.sendMessage("Le texte de ton rappel est trop long, soit plus concis ! \uD83D\uDE44").queue();
            else if (postReminder(ctx, remindText.toString(), date, ctx.getAuthor().getId()))
                channel.sendMessage("Ton rappel personnel a correctement été défini ! \uD83D\uDE42").queue();

        } else if (ctx.getArgs().size() == 2 && ctx.getArgs().get(0).equalsIgnoreCase("delete")) {

            try{
                int id_reminder = Integer.parseInt(ctx.getArgs().get(1));
                Reminder reminder = reminderDAO.findById(id_reminder);

                if(reminder != null && (reminder.getAuthor().equals(ctx.getAuthor().getId()) || ctx.getAuthor().getId().equals(ctx.getGuild().getOwnerId()))){
                    boolean res = reminderDAO.delete(reminder);
                    if(res){
                        ctx.getChannel().sendMessage(reminder.getName()+" a bien été supprimé \uD83D\uDE42").queue();
                    }
                    else
                        ctx.getChannel().sendMessage("❌ Une erreur interne est apparu lors de la suppression de ton rappel..").queue();
                }
                else
                    ctx.getChannel().sendMessage("❌ Tu n'as pas de privilèges pour supprimer ce rappel (s'il existe)").queue();


            }catch (NumberFormatException exception){
                LOGGER.error(exception.getMessage());
                ctx.getChannel().sendMessage("❌ L'identifiant pour la supression du rappel n'est pas correct, j'attends un nombre").queue();
            }

        }
        else if (ctx.getArgs().size() >= 1 && ctx.getArgs().get(0).equalsIgnoreCase("-list")) {

            boolean printIdentifiant = false;

            if(ctx.getArgs().size() >= 2 && ctx.getArgs().get(1).equalsIgnoreCase("id"))
                printIdentifiant = true;

            ArrayList<Reminder> reminders = reminderDAO.findByGuild(ctx.getEvent().getGuild());

            Collections.sort(reminders);
            if (reminders.size() > 0) {


                for(EmbedBuilder embedBuilder : constructList(reminders,false, printIdentifiant)){
                    ctx.getChannel().sendMessage(embedBuilder.build()).queue();

                }

            } else
                ctx.getChannel().sendMessage("Il n'y a pas de rappel sur le serveur \uD83D\uDE42").queue();

        }
        else{
            ctx.getChannel().sendMessage("Tu sembles rencontrer des problèmes avec la commande.. Consulte l'aide ! \uD83D\uDE42").queue();
        }

    }

    /**
     * Parse {@code String} object into a {@code Date} object.
     *
     * <br>The date format string must be of this type : {@code dd/MM/yyyy}.
     * <br>The time format string must be of this type : {@code hh:mm}.
     * @param date the date
     * @param time the time
     * @return {@code Date} object if the parse is successful, null otherwise
     */
    private Date stringToDate(String date, String time) {
        try{
            String parse = date+" "+time;
            return new SimpleDateFormat("dd/MM/yyyy hh:mm").parse(parse);
        }catch (Exception e) {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "remind";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getHelp() {
        return "Permet d'utiliser les rappels.\n"+
                "__Utilisation sur serveur :__\n\t `"+ Config.get("prefix")+getName()+" [[{channel}] {dd}/{MM}/{yyyy} {hh}:{mm} {name}] | [-list [id]] | [-delete {id}] `\n\n"+
                "__Utilisation par message privée :__\n\t `"+ Config.get("prefix")+getName()+" [{dd}/{MM}/{yyyy} {hh}:{mm} {name}] | [-list [id]] | [-delete {id}] | [-next] `";
    }

    /**
     * Post a reminder for a guild on the database.
     *
     * @param ctx           the guild command context
     * @param name          the reminder name
     * @param date          the reminder date
     * @param recipient     the reminder recipient
     * @return {@code true} if the publish operation is successful, {@code false} otherwise
     */
    private boolean postReminder(GuildCommandContext ctx, String name, Date date, String recipient){

        OGuild guild;
        if(recipient.equals(ctx.getAuthor().getId()))
            guild = null;
        else
            guild = new OGuild(ctx.getGuild().getId());

        Reminder reminder = new Reminder(
                name,
                date,
                ctx.getAuthor().getId(),
                recipient,
                guild
        );

        ReminderDAO reminderDAO = (ReminderDAO) DAOFactory.getReminder();
        Reminder res = reminderDAO.create(reminder);

        return res != null;
    }

    /**
     * Post a private reminder on the database.
     *
     * @param ctx   the private command context
     * @param name  the reminder name
     * @param date  the reminder date
     * @return {@code true} if the publish operation is successful, {@code false} otherwise
     */
    private boolean postReminder(PrivateCommandContext ctx, String name, Date date){

        Reminder reminder = new Reminder(
                name,
                date,
                ctx.getAuthor().getId(),
                ctx.getAuthor().getId(),
                null
        );

        ReminderDAO reminderDAO = (ReminderDAO) DAOFactory.getReminder();
        Reminder res = reminderDAO.create(reminder);

        return res != null;
    }

    /**
     * Allows you to build the message to display the list of reminders.
     *
     * @param reminders     the list of reminders to display in the message
     * @param isPrivate     are they private or guild reminder ?
     * @param displayIDs    display the reminder IDs or not?
     * @return the list of {@code EmbedBuilder} to build the message
     */
    private ArrayList<EmbedBuilder> constructList(ArrayList<Reminder> reminders, boolean isPrivate, boolean displayIDs){


        ArrayList<EmbedBuilder> embedBuilders = new ArrayList<>();
        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("Liste des rappels");
        eb.setColor(new Color(0x055B89));

        EmbedBuilder temp;

        Map<String, List<Reminder>> reminderListGrouped =
                reminders.stream().collect(Collectors.groupingBy(Reminder::getFormattedDateForComparison));
        SortedSet<String> keys = new TreeSet<>(reminderListGrouped.keySet());
        for (String key : keys) {
            List<Reminder> reminderList = reminderListGrouped.get(key);
            Collections.sort(reminderList);

            MessageEmbed.Field field = new MessageEmbed.Field(
                    " *• Le "+reminderList.get(0).getDay()+"*",
                    "",
                    false
            );

            temp = new EmbedBuilder(eb);
            temp.addField(field);

            if(!temp.isValidLength()) {
                embedBuilders.add(eb);
                eb = new EmbedBuilder();
            }
            eb.addField(field);

            for (Reminder reminder : reminderList) {

                String title =reminder.getName();
                String content = reminder.getTime();
                if(displayIDs)
                    title = title+" ("+reminder.getId()+")";
                if(!isPrivate){
                   content = content + "\n" + "<#" + reminder.getRecipient() + ">";
                   content = content + "\n Ajouté par <@"+reminder.getAuthor() + ">";
                }

                MessageEmbed.Field field2 = new MessageEmbed.Field(
                    title,
                    content,
                    true
                );


                temp = new EmbedBuilder(eb);
                temp.addField(field2);

                if(!temp.isValidLength()) {
                    embedBuilders.add(eb);
                    eb = new EmbedBuilder();
                }
                eb.addField(field2);

            }

            temp = new EmbedBuilder(eb);
            temp.addBlankField(false);

            if(!temp.isValidLength()) {
                embedBuilders.add(eb);
                eb = new EmbedBuilder();
            }
            eb.addBlankField(false);

        }

        embedBuilders.add(eb);

        return embedBuilders;
    }

}

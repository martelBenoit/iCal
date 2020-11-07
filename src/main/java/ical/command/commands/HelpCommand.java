package ical.command.commands;

import ical.command.*;
import ical.manager.CommandManager;
import ical.util.Config;

import java.util.ArrayList;
import java.util.List;

/**
 * HelpCommand class.
 *
 * @author Benoît Martel
 * @version 1.0
 * @since 1.0
 */
public class HelpCommand implements IGuildCommand, IPrivateCommand {

    /**
     * the command manager.
     */
    private final CommandManager manager;

    /**
     * Default constructor.
     *
     * @param manager the command manager
     */
    public HelpCommand(CommandManager manager) {
        this.manager = manager;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void handle(GuildCommandContext ctx) {

            List<String> args = ctx.getArgs();

            if (args.isEmpty()) {

                ArrayList<StringBuilder> messages = new ArrayList<>();

                StringBuilder builder = new StringBuilder();
                StringBuilder temp;


                builder.append("__**Liste des commandes du bot iCal**__\n\n");

                for (ICommand command : manager.getCommands()) {
                    temp = new StringBuilder();
                    if (!command.getName().equalsIgnoreCase("help")) {
                        temp.append('`').append(command.getName()).append("`\n");
                        temp.append(command.getHelp());
                        temp.append("\n\n");
                    }
                    if(builder.length()+temp.length() > 2000){
                        messages.add(builder);
                        builder = new StringBuilder();
                        builder.append("\n");
                        builder.append(temp);
                    }
                    else{
                        builder.append(temp);
                    }

                }
                messages.add(builder);

                for(StringBuilder stringBuilder : messages){
                    ctx.getEvent().getAuthor().openPrivateChannel().queue((cha) -> cha.sendMessage(stringBuilder.toString()).queue());
                }



            }


    }

    @Override
    public void handle(PrivateCommandContext ctx) {

        List<String> args = ctx.getArgs();

        if (args.isEmpty()) {

            StringBuilder builder = new StringBuilder();

            builder.append("__**Liste des commandes du bot iCal**__\n\n");

            for (ICommand command : manager.getCommands()) {
                if (!command.getName().equalsIgnoreCase("help")) {
                    builder.append('`').append(command.getName()).append("`\n");
                    builder.append(command.getHelp());
                    builder.append("\n\n");
                }

            }

            ctx.getChannel().sendMessage(builder.toString()).queue();

        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "help";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getHelp() {
        return "Affiche la liste des commandes disponibles d'iCal\n" +
                "Utilisation : `"+Config.get("prefix")+getName()+"`";
    }


}

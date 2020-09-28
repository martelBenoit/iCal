package ical.command.commands;

import ical.manager.CommandManager;
import ical.util.Config;
import ical.command.CommandContext;
import ical.command.ICommand;

import java.util.List;

/**
 * HelpCommand class.
 *
 * @author Benoît Martel
 * @version 1.0
 * @since 1.0
 */
public class HelpCommand implements ICommand {

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
    public void handle(CommandContext ctx) {
        List<String> args = ctx.getArgs();

        if(args.isEmpty()){

            StringBuilder builder = new StringBuilder();

            builder.append("__**Liste des commandes du bot iCal**__\n\n");

            for(ICommand command : manager.getCommands()){
                if(!command.getName().equalsIgnoreCase("help")){
                    builder.append('`').append(command.getName()).append("`\n");
                    builder.append(command.getHelp());
                    builder.append("\n\n");
                }

            }

           ctx.getEvent().getAuthor().openPrivateChannel().queue((cha) -> cha.sendMessage(builder.toString()).queue());

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

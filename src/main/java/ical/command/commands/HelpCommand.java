package ical.command.commands;

import ical.manager.CommandManager;
import ical.util.Config;
import ical.command.CommandContext;
import ical.command.ICommand;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;

public class HelpCommand implements ICommand {


    private final CommandManager manager;

    public HelpCommand(CommandManager manager) {
        this.manager = manager;
    }


    @Override
    public void handle(CommandContext ctx) {
        List<String> args = ctx.getArgs();
        TextChannel channel = ctx.getChannel();

        if(args.isEmpty()){

            StringBuilder builder = new StringBuilder();

            builder.append("__**Liste des commandes du bot ICal**__\n\n");

            manager.getCommands().stream().map(ICommand::getName).forEach(
                    it -> builder.append('`').append(Config.get("prefix")).append(it).append("`\n")

            );

            channel.sendMessage(builder.toString()).queue();
            return;
        }

        String search = args.get(0);
        ICommand command = manager.getCommand(search);

        if(command == null){
            channel.sendMessage("J'ai rien trouvé pour `"+ search+"`").queue();
            return;
        }

        channel.sendMessage(command.getHelp()).queue();
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getHelp() {
        return "Affiche la liste des commandes disponibles d'ICal\n" +
                "Utilisation : `"+Config.get("prefix")+"help [commande]`";
    }
}

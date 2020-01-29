package ical.command;

import me.duncte123.botcommons.commands.ICommandContext;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;

/**
 * CommandContext class. Implement ICommandContext
 * <br> Gère le contexte d'une commande, permet notamment de récupérer l'évènement.
 *
 * @author Benoît Martel
 * @version 1.0
 * @see ICommandContext
 */
public class CommandContext implements ICommandContext {

    private final GuildMessageReceivedEvent event;
    private final List<String> args;

    /**
     * Constructor.
     *
     * @param event the guild message received event
     * @param args the list of arguments of the command
     */
    public CommandContext(GuildMessageReceivedEvent event, List<String> args) {
        this.event = event;
        this.args = args;
    }

    @Override
    public Guild getGuild() {
        return this.getEvent().getGuild();
    }

    @Override
    public GuildMessageReceivedEvent getEvent() {
        return this.event;
    }

    /**
     * Get the list of arguments of the command
     * @return the command arguments list
     */
    public List<String> getArgs() {
        return this.args;
    }
}
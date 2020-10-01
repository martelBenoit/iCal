package ical.command;

import net.dv8tion.jda.api.entities.Guild;

import me.duncte123.botcommons.commands.ICommandContext;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;

/**
 * CommandContext class. Implement {@link ICommandContext}
 * <br> Manages the context of an order, in particular allows you to retrieve the event.
 *
 * @author Benoît Martel
 * @version 1.1
 * @see ICommandContext
 */
public class GuildCommandContext extends CommandContext implements ICommandContext{

    /**
     * the guild message received event
     */
    private final GuildMessageReceivedEvent event;


    /**
     * Constructor.
     *
     * @param event the guild message received event
     * @param args the list of arguments of the command
     */
    public GuildCommandContext(GuildMessageReceivedEvent event, List<String> args) {
        super(args);
        this.event = event;
    }

    /**
     * Find the guild from which the event originates
     *
     * @return the guild
     */
    @Override
    public Guild getGuild() {
        return this.getEvent().getGuild();
    }

    /**
     * Get the event.
     *
     * @return the event
     */
    @Override
    public GuildMessageReceivedEvent getEvent() {
        return this.event;
    }

    /**
     * Get the list of arguments of the command
     *
     * @return the command arguments list
     */
    @Override
    public List<String> getArgs() {
        return this.args;
    }

}
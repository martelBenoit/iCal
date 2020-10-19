package ical.command;

import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * PrivateCommandContext class.
 *
 * @author Benoît Martel
 * @version 1.0
 * @since 1.8
 * @see CommandContext
 */
public class PrivateCommandContext extends CommandContext {

    /**
     * the private message received event
     */
    private final PrivateMessageReceivedEvent event;

    /**
     * Constructor.
     *  @param event    the private message received event
     * @param args      the list of arguments of the command
     */
    public PrivateCommandContext(PrivateMessageReceivedEvent event, @Nonnull List<String> args) {
        super(args);
        this.event = event;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PrivateMessageReceivedEvent getEvent() {
        return this.event;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public List<String> getArgs() {
        return this.args;
    }

    /**
     * Get the private channel of the event.
     *
     * @return the private channel of the event
     */
    public PrivateChannel getChannel(){
        return this.event.getChannel();
    }

    /**
     * Get the author of the event.
     *
     * @return the author of the event
     */
    public User getAuthor(){
        return this.event.getAuthor();
    }

}

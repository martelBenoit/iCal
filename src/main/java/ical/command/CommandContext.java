package ical.command;

import net.dv8tion.jda.api.events.Event;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * CommandContext abstract class.
 *
 * @author Benoît Martel
 * @version 1.1
 * @since 1.0
 */
public abstract class CommandContext {

    /**
     * the argument list
     */
    protected final List<String> args;

    /**
     * Default constructor.
     *
     * @param args the args list
     */
    public CommandContext(@Nonnull List<String> args) {

        this.args = args;
    }

    /**
     * Get the event.
     *
     * @return the event
     */
    public abstract Event getEvent();

    /**
     * Get the list of arguments of the command
     *
     * @return the command arguments list
     */
    @Nonnull
    public abstract List<String> getArgs();

}

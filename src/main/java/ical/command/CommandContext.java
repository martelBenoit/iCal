package ical.command;

import net.dv8tion.jda.api.events.Event;

import java.util.List;

public abstract class CommandContext {

    /**
     * the argument list
     */
    protected final List<String> args;

    public CommandContext(List<String> args) {

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
    public abstract List<String> getArgs();

}

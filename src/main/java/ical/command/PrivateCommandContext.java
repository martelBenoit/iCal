package ical.command;

import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

import java.util.List;

public class PrivateCommandContext extends CommandContext {

    /**
     * the private message received event
     */
    private final PrivateMessageReceivedEvent event;

    /**
     * Constructor.
     *  @param event the private message received event
     * @param args the list of arguments of the command
     */
    public PrivateCommandContext(PrivateMessageReceivedEvent event, List<String> args) {
        super(args);
        this.event = event;

    }

    /**
     * Get the event.
     *
     * @return the event
     */
    @Override
    public PrivateMessageReceivedEvent getEvent() {
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

    public PrivateChannel getChannel(){
        return this.event.getChannel();
    }

    public User getAuthor(){
        return this.event.getAuthor();
    }



}

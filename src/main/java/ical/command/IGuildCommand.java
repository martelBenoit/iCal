package ical.command;

/**
 * IGuildCommand interface.
 *
 * @author Benoît Martel
 * @version 1.0
 * @since 1.0
 */
public interface IGuildCommand extends ICommand {

    /**
     * Method invoke to trigger an execution and / or a treatment for any command.
     *
     * @see GuildCommandContext
     * @param ctx the command context
     */
    void handle(GuildCommandContext ctx);


}
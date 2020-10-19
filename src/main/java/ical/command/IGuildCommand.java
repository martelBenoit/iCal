package ical.command;

/**
 * IGuildCommand interface.
 *
 * @author Benoît Martel
 * @version 1.0
 * @since 1.0
 * @see ICommand
 */
public interface IGuildCommand extends ICommand {

    /**
     * Method invoke to trigger an execution and / or a treatment for a guild command context.
     *
     * @see GuildCommandContext
     * @param ctx the guild command context
     */
    void handle(GuildCommandContext ctx);

}
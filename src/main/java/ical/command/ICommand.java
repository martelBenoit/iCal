package ical.command;

/**
 * ICommand interface.
 *
 * @author Benoît Martel
 * @version 1.0
 * @since 1.0
 */
public interface ICommand {

    /**
     *
     * @see CommandContext
     * @param ctx the command context
     */
    void handle(CommandContext ctx);

    /**
     * Get the name of the command.
     *
     * @return the name of the command
     */
    String getName();

    /**
     * Get the help of the command.
     *
     * @return the help of the command
     */
    String getHelp();


}
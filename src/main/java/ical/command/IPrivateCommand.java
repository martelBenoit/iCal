package ical.command;

/**
 * IPrivateCommand interface.
 *
 * @author Benoît Martel
 * @version 1.0
 * @since 1.8
 * @see ICommand
 */
public interface IPrivateCommand extends ICommand {

    /**
     * Method invoke to trigger an execution and / or a treatment for private command context.
     *
     * @see PrivateCommandContext
     * @param ctx the command context
     */
    void handle(PrivateCommandContext ctx);

}

package ical.command;

public interface IPrivateCommand extends ICommand {

    /**
     * Method invoke to trigger an execution and / or a treatment for any command.
     *
     * @see PrivateCommandContext
     * @param ctx the command context
     */
    void handle(PrivateCommandContext ctx);
}

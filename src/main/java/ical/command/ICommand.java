package ical.command;

/**
 * ICommand interface.
 *
 * @version 1.1
 * @since 1.0
 * @author Benoît Martel
 */
public interface ICommand {

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

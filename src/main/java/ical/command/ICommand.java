package ical.command;

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

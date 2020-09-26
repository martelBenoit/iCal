package ical.command.commands;

import ical.command.ICommand;
import ical.manager.ScheduleManager;

/**
 * AbstractScheduleCommand class.
 *
 * This class implements {@link ICommand} interface
 *
 * @author Benoît Martel
 * @version 1.0
 * @since 1.0
 */
public abstract class AbstractScheduleCommand implements ICommand {

    /**
     * the schedule manager
     */
    protected final ScheduleManager scheduleManager;

    /**
     * Default constructor.
     * @param scheduleManager the schedule manager
     */
    public AbstractScheduleCommand(ScheduleManager scheduleManager){
        this.scheduleManager = scheduleManager;
    }
}

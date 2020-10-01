package ical.command.commands;

import ical.command.IGuildCommand;
import ical.manager.ScheduleManager;

/**
 * AbstractScheduleCommand class.
 *
 * This class implements {@link IGuildCommand} interface
 *
 * @author Benoît Martel
 * @version 1.1
 * @since 1.0
 */
public abstract class AbstractScheduleCommand implements IGuildCommand {

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

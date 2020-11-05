package ical.command.commands;

import ical.command.IGuildCommand;
import ical.manager.ScheduleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractScheduleCommand.class);

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

package ical.command.commands;

import ical.command.ICommand;
import ical.manager.ScheduleManager;

public abstract class AbstractScheduleCommand implements ICommand {

    protected final ScheduleManager scheduleManager;

    public AbstractScheduleCommand(ScheduleManager scheduleManager){
        this.scheduleManager = scheduleManager;
    }
}

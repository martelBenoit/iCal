package ical.command.commands;

import ical.command.ICommand;
import ical.manager.ScheduleManager;

public abstract class AbtractScheduleCommand implements ICommand {

    protected final ScheduleManager scheduleManager;

    public AbtractScheduleCommand(ScheduleManager scheduleManager){
        this.scheduleManager = scheduleManager;
    }
}

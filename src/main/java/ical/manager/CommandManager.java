package ical.manager;

import ical.command.ICommand;
import ical.command.IGuildCommand;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public abstract class CommandManager {

    private final List<ICommand> commands = new ArrayList<>();


    protected void addCommand(ICommand cmd){
        boolean nameFound = this.commands.stream().anyMatch(it -> it.getName().equalsIgnoreCase(cmd.getName()));

        if(nameFound){
            throw new IllegalArgumentException("A command with this name is already present");
        }

        commands.add(cmd);
    }

    public List<ICommand> getCommands(){
        return commands;
    }

    @Nullable
    public ICommand getCommand(String search){

        ICommand ret = null;

        for (ICommand command : this.commands) {
            if(command.getName().equalsIgnoreCase(search)){
                ret = command;
            }
        }

        return ret;

    }

}

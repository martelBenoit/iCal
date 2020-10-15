package ical.manager;

import ical.command.ICommand;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.GenericEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * CommandManager abstract class.
 *
 * @version 1.1
 * @since 1.0
 * @author Benoît Martel
 */
public abstract class CommandManager {

    /**
     * activated commands list.
     */
    private final List<ICommand> commands = new ArrayList<>();

    /**
     * Allows you to add a command to activate.<br>
     * The command is placed in the list
     * @param cmd the command to activate
     */
    protected void addCommand(@Nonnull ICommand cmd){

        if(this.commands.stream().anyMatch(it -> it.getName().equalsIgnoreCase(cmd.getName()))){
            throw new IllegalArgumentException("A command with this name is already present");
        }

        commands.add(cmd);
    }

    /**
     * Get activated commands list.
     *
     * @return activated commands list
     */
    @Nonnull
    public List<ICommand> getCommands(){
        return commands;
    }


    /**
     * Retrieve an command by name.
     *
     * @param search the command name
     * @return {@code ICommand} if command found, {@code null} otherwise
     */
    @Nullable
    public ICommand getCommand(@Nonnull String search){

        return this.commands.stream().filter(c -> c.getName().equalsIgnoreCase(search)).findFirst().orElse(null);

    }

}

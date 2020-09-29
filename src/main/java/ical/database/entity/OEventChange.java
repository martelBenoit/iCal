package ical.database.entity;

import ical.util.ModificationType;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

/**
 * OEventChange class.
 * <br>A schedule modification event is composed of an id, a date and a type of modification.
 *
 * @see ModificationType
 * @author Benoît Martel
 * @version 1.0
 */
public class OEventChange extends Entity{

    /**
     * the event change id.
     */
    private final String id;

    /**
     * the event change date.
     */
    private final Date date;

    /**
     * the event change type.
     */
    private final ModificationType type;

    /**
     * Constructor.
     *
     * @param id    the id of the event change
     * @param date  the date of the event change
     * @param type  the type of the event change
     */
    public OEventChange(@NotNull String id, @NotNull Date date, @NotNull ModificationType type) {
        this.id = id;
        this.date = date;
        this.type = type;
    }

    /**
     * Get the id of the event change
     * @return the id
     */
    @NotNull
    public String getId() {
        return id;
    }

    /**
     * Get the date of the event
     * @return the date
     */
    @NotNull
    public Date getDate() {
        return date;
    }

    /**
     * Get the type of the event
     * @return the modification type of the event
     */
    @NotNull
    public ModificationType getType(){
        return type;
    }

}

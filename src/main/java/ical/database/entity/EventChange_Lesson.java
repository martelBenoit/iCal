package ical.database.entity;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

/**
 * EventChange_Lesson entity.
 *
 * <br> This entity allows to link an event with the {@link MovedLesson} entity.
 * @author Benoît Martel
 * @version 1.0
 * @since 1.4
 */
public class EventChange_Lesson extends Entity {

    /**
     * the event change entity.
     */
    private final OEventChange eventChange;

    /**
     * the moved lesson entity.
     */
    private final MovedLesson movedLesson;

    /**
     * Constructor.
     *
     * @param eventChange the event change entity
     * @param movedLesson the moved lesson entity
     */
    public EventChange_Lesson(@Nonnull OEventChange eventChange, @Nonnull MovedLesson movedLesson) {
        this.eventChange = eventChange;
        this.movedLesson = movedLesson;
    }

    /**
     * Get the event change entity.
     *
     * @return the event change entity
     */
    @NotNull
    public OEventChange getEventChange() {
        return eventChange;
    }

    /**
     * Get the moved lesson entity.
     *
     * @return the moved lesson entity
     */
    @NotNull
    public MovedLesson getMovedLesson() {
        return movedLesson;
    }

}

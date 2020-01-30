package ical.database.entity;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class EventChange_Lesson {

    private final OEventChange eventChange;
    private final MovedLesson movedLesson;

    public EventChange_Lesson(@Nonnull OEventChange eventChange, @Nonnull MovedLesson movedLesson) {
        this.eventChange = eventChange;
        this.movedLesson = movedLesson;
    }

    @NotNull
    public OEventChange getEventChange() {
        return eventChange;
    }

    @NotNull
    public MovedLesson getMovedLesson() {
        return movedLesson;
    }

}
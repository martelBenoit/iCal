package ical.database.entity;

public class EventChange_Lesson {

    private OEventChange eventChange;
    private MovedLesson movedLesson;

    public EventChange_Lesson(OEventChange eventChange, MovedLesson movedLesson) {
        this.eventChange = eventChange;
        this.movedLesson = movedLesson;
    }

    public OEventChange getEventChange() {
        return eventChange;
    }

    public void setEventChange(OEventChange eventChange) {
        this.eventChange = eventChange;
    }

    public MovedLesson getMovedLesson() {
        return movedLesson;
    }

    public void setMovedLesson(MovedLesson movedLesson) {
        this.movedLesson = movedLesson;
    }
}

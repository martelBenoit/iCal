package ical.database.entity;

import javax.annotation.Nullable;

public class MovedLesson {

    private int id;

    private Lesson previousLesson;
    private Lesson actualLesson;

    public MovedLesson(int id, @Nullable Lesson previousLesson, @Nullable Lesson actualLesson) {
        this.id = id;
        this.previousLesson = previousLesson;
        this.actualLesson = actualLesson;
    }

    public MovedLesson(@Nullable Lesson previousLesson, @Nullable Lesson actualLesson) {
        this.id = -1;
        this.previousLesson = previousLesson;
        this.actualLesson = actualLesson;
    }

    public int getId(){
        return this.id;
    }

    public void setId(int id){
        this.id = id;
    }

    @Nullable
    public Lesson getPreviousLesson() {
        return previousLesson;
    }

    public void setPreviousLesson(Lesson previousLesson) {
        this.previousLesson = previousLesson;
    }

    @Nullable
    public Lesson getActualLesson() {
        return actualLesson;
    }

    public void setActualLesson(Lesson actualLesson) {
        this.actualLesson = actualLesson;
    }
}

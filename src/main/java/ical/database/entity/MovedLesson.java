package ical.database.entity;


public class MovedLesson {

    private int id;

    private Lesson previousLesson;
    private Lesson actualLesson;

    public MovedLesson(int id, Lesson previousLesson, Lesson actualLesson) {
        this.id = id;
        this.previousLesson = previousLesson;
        this.actualLesson = actualLesson;
    }

    public MovedLesson(Lesson previousLesson, Lesson actualLesson) {
        this.previousLesson = previousLesson;
        this.actualLesson = actualLesson;
    }

    public int getId(){
        return this.id;
    }

    public void setId(int id){
        this.id = id;
    }


    public Lesson getPreviousLesson() {
        return previousLesson;
    }

    public void setPreviousLesson(Lesson previousLesson) {
        this.previousLesson = previousLesson;
    }

    public Lesson getActualLesson() {
        return actualLesson;
    }

    public void setActualLesson(Lesson actualLesson) {
        this.actualLesson = actualLesson;
    }
}

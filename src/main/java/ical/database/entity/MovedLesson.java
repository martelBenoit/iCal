package ical.database.entity;

import javax.annotation.Nullable;

/**
 * MovedLesson entity class.
 *
 * @author Benoît Martel
 * @version 1.0
 */
public class MovedLesson extends Entity{

    /**
     * the moved lesson id.
     */
    private int id;

    /**
     * the previous lesson.
     */
    private Lesson previousLesson;

    /**
     * the actual lesson.
     */
    private Lesson actualLesson;

    /**
     * Default constructor.
     *
     * @param previousLesson the previous lesson
     * @param actualLesson the actual lesson
     */
    public MovedLesson(@Nullable Lesson previousLesson, @Nullable Lesson actualLesson) {
        this.id = -1;
        this.previousLesson = previousLesson;
        this.actualLesson = actualLesson;
    }

    /**
     * Get the moved lesson id.
     *
     * @return the id
     */
    public int getId(){
        return this.id;
    }

    /**
     * Set the moved lesson id.
     *
     * @param id the new moved lesson id
     */
    public void setId(int id){
        this.id = id;
    }

    /**
     * Get the previous lesson.
     *
     * @return the previous lesson
     */
    @Nullable
    public Lesson getPreviousLesson() {
        return previousLesson;
    }

    /**
     * Set the previous lesson.
     *
     * @param previousLesson the previous lesson
     */
    public void setPreviousLesson(Lesson previousLesson) {
        this.previousLesson = previousLesson;
    }

    /**
     * Get the actual lesson.
     *
     * @return the actual lesson
     */
    @Nullable
    public Lesson getActualLesson() {
        return actualLesson;
    }

    /**
     * Set the actual lesson.
     *
     * @param actualLesson the actual lesson
     */
    public void setActualLesson(Lesson actualLesson) {
        this.actualLesson = actualLesson;
    }
}

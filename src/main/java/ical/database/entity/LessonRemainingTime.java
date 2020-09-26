package ical.database.entity;

/**
 * LessonRemainingTime entity.
 *
 * @version 1.0
 * @author Benoît Martel
 */
public class LessonRemainingTime extends Entity {

    /**
     * a lesson.
     */
    private Lesson lesson;

    /**
     * message id.
     */
    private final Long id_message;

    /**
     * channel id.
     */
    private final Long id_channel;

    /**
     * Constructor.
     *
     * @param lesson the lesson linked to the message id
     * @param id_message the id message
     * @param id_channel the id channel linked to the message id
     */
    public LessonRemainingTime(Lesson lesson, Long id_message, Long id_channel){
        this.lesson = lesson;
        this.id_message = id_message;
        this.id_channel = id_channel;
    }

    /**
     * Get the lesson.
     *
     * @return the lesson
     */
    public Lesson getLesson() {
        return lesson;
    }

    /**
     * Set the lesson.
     *
     * @param lesson the lesson
     */
    public void setLesson(Lesson lesson) {
        this.lesson = lesson;
    }

    /**
     * Get the id message.
     *
     * @return the id message
     */
    public Long getId_message() {
        return id_message;
    }

    /**
     * Get the id channel.
     *
     * @return the id channel
     */
    public Long getId_channel() {
        return id_channel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "LessonRemainingTime{" +
                "lesson=" + lesson +
                ", id_message=" + id_message +
                ", id_channel=" + id_channel +
                '}';
    }
}

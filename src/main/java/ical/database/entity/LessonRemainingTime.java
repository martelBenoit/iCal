package ical.database.entity;

import net.dv8tion.jda.api.entities.MessageEmbed;

public class LessonRemainingTime {

    private Lesson lesson;

    private Long id_message;

    private Long id_channel;

    private MessageEmbed message;

    public LessonRemainingTime(Lesson lesson, Long id_message, Long id_channel){
        this.lesson = lesson;
        this.id_message = id_message;
        this.id_channel = id_channel;
    }

    public Lesson getLesson() {
        return lesson;
    }

    public void setLesson(Lesson lesson) {
        this.lesson = lesson;
    }

    public Long getId_message() {
        return id_message;
    }

    public void setId_message(Long id_message) {
        this.id_message = id_message;
    }

    public Long getId_channel() {
        return id_channel;
    }

    public void setId_channel(Long id_channel) {
        this.id_channel = id_channel;
    }


}

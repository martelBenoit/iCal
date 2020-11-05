package ical.database.entity;

import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;

/**
 * Professor_Picture_By_Guild class.
 *
 *
 * @author Benoît Martel
 * @version 1.0
 */
public class Professor_Picture_By_Guild extends Entity {

    private OGuild guild;

    private Professor professor;

    private String url;

    private String id_user;

    private Timestamp last_modification;

    public Professor_Picture_By_Guild(@NotNull OGuild guild, @NotNull Professor professor, @NotNull String url, @NotNull String id_user) {
        this.guild = guild;
        this.professor = professor;
        this.url = url;
        this.id_user = id_user;
    }

    public Professor_Picture_By_Guild(@NotNull OGuild guild, @NotNull Professor professor, @NotNull String url, @NotNull String id_user, Timestamp timestamp) {
        this.guild = guild;
        this.professor = professor;
        this.url = url;
        this.id_user = id_user;
        this.last_modification = timestamp;
    }

    public OGuild getGuild() {
        return guild;
    }

    public void setGuild(OGuild guild) {
        this.guild = guild;
    }

    public Professor getProfessor() {
        return professor;
    }

    public void setProfessor(Professor professor) {
        this.professor = professor;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId_user() {
        return id_user;
    }

    public void setId_user(String id_user) {
        this.id_user = id_user;
    }

    public Timestamp getLast_modification() {
        return last_modification;
    }

    public void setLast_modification(Timestamp last_modification) {
        this.last_modification = last_modification;
    }
}

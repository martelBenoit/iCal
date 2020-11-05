package ical.database.entity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Timestamp;

/**
 * Professor_Picture_By_Guild class.
 *
 * @author Benoît Martel
 * @version 1.0
 */
public class Professor_Picture_By_Guild extends Entity {

    /**
     * the guild instance.
     */
    private OGuild guild;

    /**
     * the professor instance.
     */
    private Professor professor;

    /**
     * the profile picture url.
     */
    private String url;

    /**
     * the user id.
     */
    private String id_user;

    /**
     * the last modification.
     */
    private Timestamp last_modification;

    /**
     * Default constructor.
     * <br>Use when creating an object from nothing.
     *
     * @param guild     the guild
     * @param professor the professor
     * @param url       the profile picture
     * @param id_user   the user id
     */
    public Professor_Picture_By_Guild(@NotNull OGuild guild, @NotNull Professor professor, @NotNull String url, @NotNull String id_user) {
        this.guild = guild;
        this.professor = professor;
        this.url = url;
        this.id_user = id_user;
    }

    /**
     * Constructor.
     *
     * @param guild     the guild
     * @param professor the professor
     * @param url       the profile picture
     * @param id_user   the user id
     * @param timestamp the last modification
     */
    public Professor_Picture_By_Guild(@NotNull OGuild guild, @NotNull Professor professor, @NotNull String url, @NotNull String id_user, Timestamp timestamp) {
        this.guild = guild;
        this.professor = professor;
        this.url = url;
        this.id_user = id_user;
        this.last_modification = timestamp;
    }

    /**
     * Get the guild.
     *
     * @return the guild
     */
    @NotNull
    public OGuild getGuild() {
        return guild;
    }

    /**
     * Set the guild.
     *
     * @param guild the new guild.
     */
    public void setGuild(@NotNull OGuild guild) {
        this.guild = guild;
    }

    /**
     * Get the professor.
     *
     * @return the professor
     */
    @NotNull
    public Professor getProfessor() {
        return professor;
    }

    /**
     * Set the professor.
     *
     * @param professor the new professor
     */
    public void setProfessor(@NotNull Professor professor) {
        this.professor = professor;
    }

    /**
     * Get the profile picture url.
     *
     * @return the profile picture url
     */
    @NotNull
    public String getUrl() {
        return url;
    }

    /**
     * Set the profile picture url.
     *
     * @param url the profile picture url
     */
    public void setUrl(@NotNull String url) {
        this.url = url;
    }

    /**
     * Get the id user.
     *
     * @return the id user
     */
    @NotNull
    public String getId_user() {
        return id_user;
    }

    /**
     * Set the id user.
     *
     * @param id_user the id user
     */
    public void setId_user(@NotNull String id_user) {
        this.id_user = id_user;
    }

    /**
     * Get the last modification.
     *
     * @return the last modification
     */
    @Nullable
    public Timestamp getLast_modification() {
        return last_modification;
    }

    /**
     * Set the last modification.
     *
     * @param last_modification the last modification
     */
    public void setLast_modification(@NotNull Timestamp last_modification) {
        this.last_modification = last_modification;
    }
}

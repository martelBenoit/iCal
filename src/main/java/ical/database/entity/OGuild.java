package ical.database.entity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Represent a guild connected to the bot.
 * Allow to manage the comportment of the bot for the guild.
 *
 * @author Benoît Martel
 * @version 1.1
 */
public class OGuild {

    /**
     * the id of the guild
     */
    private final String idGuild;

    /**
     * the id of the default channel
     */
    private String idChannel;

    /**
     * the url of the schedule
     */
    private String urlSchedule;

    private boolean modifNotif;

    private boolean lessonNotif;

    /**
     * Constructor.
     * Allow to create an object only with the id of the guild. The id of default channel and the url of the schedule
     * are null.
     *
     * @param idGuild the id of the guild
     */
    public OGuild(@Nonnull String idGuild) {
        this.idGuild = idGuild;
    }

    /**
     * Contructor.
     * Allow to create an object with the id of the guild, id of the channel and the url of the schedule.
     *
     * @param idGuild the id of the guild
     * @param idChannel the id of the channel
     * @param urlSchedule the url of the schedule
     * @param modifNotif the boolean which indicates whether lessons modifications notifications are active or not
     * @param lessonNotif the boolean which indicates whether lesson notifications are active or not
     */
    public OGuild(@Nonnull String idGuild, String idChannel, String urlSchedule, boolean modifNotif, boolean lessonNotif) {
        this.idGuild = idGuild;
        this.idChannel = idChannel;
        this.urlSchedule = urlSchedule;
        this.modifNotif = modifNotif;
        this.lessonNotif = lessonNotif;
    }

    /**
     * Get the guild id.
     * @return the guild id
     */
    @Nonnull
    public String getIdGuild() {
        return idGuild;
    }

    /**
     * Get the default channel id.
     * @return the default channel id
     */
    @Nullable
    public String getIdChannel() {
        return idChannel;
    }

    /**
     * Set the default channel id.
     * @param idChannel the default channel id
     */
    public void setIdChannel(@Nonnull String idChannel) {
        this.idChannel = idChannel;
    }

    /**
     * Get the schedule url.
     * @return the schedule url
     */
    @Nullable
    public String getUrlSchedule() {
        return urlSchedule;
    }

    /**
     * Set the schedule url.
     *
     * @param urlSchedule the schedule url
     */
    public void setUrlSchedule(@Nonnull String urlSchedule) {
        this.urlSchedule = urlSchedule;
    }

    /**
     * Get the value of boolean modification lesson notifications.
     *
     * @return true if lesson notifications are active
     */
    public boolean modifNotifisEnabled(){
        return this.modifNotif;
    }

    /**
     * Set the value of boolean modification lesson notifications.
     *
     * @param enable true to enable notifications
     */
    public void setModifNotif(boolean enable){
        this.modifNotif = enable;
    }

    /**
     * Get the value of boolean lesson notifications.
     *
     * @return true if lesson notifications are active
     */
    public boolean lessonNotifisEnabled(){
        return this.lessonNotif;
    }

    /**
     * Set the value of boolean lesson notifications.
     *
     * @param enable true to enable notifications
     */
    public void setLessonNotif(boolean enable){
        this.lessonNotif = enable;
    }

    /**
     * Override the default method equals.
     * Compare the id guild of the current object and the object passed in parameter.
     *
     * @param o the object to compare
     * @return true if the id guild of the two objects is equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OGuild oGuild = (OGuild) o;
        return idGuild.equals(oGuild.idGuild);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idGuild);
    }

    @Override
    public String toString() {
        return "OGuild [idGuild : "+idGuild+", idChannel : "+idChannel+", url : "+ urlSchedule +"]";
    }
}

package ical.database.entity;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Lesson entity class. Implements {@link Comparable<Lesson>}.
 *
 * <br>This class represents a lesson.
 * @author Benoît Martel
 * @version 1.4
 * @since 1.0
 */
public class Lesson extends Entity implements Comparable<Lesson>{

    /**
     * The UID provided by the ics file.
     */
    private final String UID;

    /**
     * The unique ID create for each new Lesson tuple on database.
     */
    private String uniqueID = null;

    /**
     * Lesson name.
     */
    private final String name;

    /**
     * Start date of lesson.
     */
    private final Date startDate;

    /**
     * End date of lesson.
     */
    private final Date endDate;

    /**
     * Lesson description.
     */
    private final String description;

    /**
     * Lesson professor.
     */
    private Professor professor;

    /**
     * Lesson room.
     */
    private String room;



    /**
     * Default constructor.
     *
     * @param uid the UID provided by the ics file
     * @param name lesson name
     * @param startDate start date of the lesson
     * @param endDate end date of the lesson
     * @param description lesson description
     * @param professor lesson professor
     * @param room lesson room
     */
    public Lesson(String uid,
                  String name,
                  Date startDate,
                  Date endDate,
                  String description,
                  Professor professor,
                  String room){
        this.UID = uid;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
        this.professor = professor;
        this.room = room;
    }

    /**
     * Constructor with unique ID
     * @param uid the UID provided by the ics file
     * @param uniqueID the unique ID create for each new Lesson tuple on database
     * @param name lesson name
     * @param startDate start date of the lesson
     * @param endDate end date of the lesson
     * @param description lesson description
     * @param professor lesson professor
     * @param room lesson room
     */
    public Lesson(String uid,
                  String uniqueID,
                  String name,
                  Date startDate,
                  Date endDate,
                  String description,
                  Professor professor,
                  String room){
        this.UID = uid;
        this.uniqueID = uniqueID;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
        this.professor = professor;
        this.room = room;
    }

    /**
     * Get the UID provided by the ics file.
     *
     * @return the UID
     */
    public String getUID(){
        return this.UID;
    }

    /**
     * Get the unique ID create for each new Lesson tuple on database.
     *
     * @return the unique ID
     */
    public String getUniqueID(){
        return this.uniqueID;
    }

    /**
     * Set the unique ID create for each new Lesson tuple on database.
     *
     * @param uniqueID the unique ID
     */
    public void setUniqueID(String uniqueID){
        this.uniqueID = uniqueID;
    }

    /**
     * Get lesson name.
     *
     * @return the lesson name
     */
    public String getName(){
        return this.name;
    }

    /**
     * Get start date of the lesson.
     *
     * @return the start date of the lesson
     */
    public Date getStartDate(){
        return this.startDate;
    }

    /**
     * Get end date of the lesson.
     *
     * @return the end date of the lesson
     */
    public Date getEndDate(){
        return this.endDate;
    }

    /**
     * Get the lesson description.
     *
     * @return the lesson description
     */
    public String getDescription(){
        return this.description;
    }

    /**
     * Get the lesson professor.
     *
     * @return the lesson professor
     */
    public Professor getProfessor(){
        return this.professor;
    }

    /**
     * Set the lesson professor.
     *
     * @param professor the lesson professor
     */
    public void setProfessor(Professor professor){
        this.professor = professor;
    }

    /**
     * Get the lesson room.
     *
     * @return the lesson room
     */
    public String getRoom(){
        return this.room;
    }

    /**
     * Get the lesson start time in string format (%02d:%02d).
     *
     * @return the lesson start time
     */
    public String getStartTime(){
        Calendar cal = Calendar.getInstance();
        cal.setTime(this.startDate);
        return String.format("%02d:%02d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
    }

    /**
     * Get the lesson end time in string format (%02d:%02d).
     *
     * @return the lesson end time
     */
    public String getEndTime(){
        Calendar cal = Calendar.getInstance();
        cal.setTime(this.endDate);
        return String.format("%02d:%02d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
    }

    /**
     * Get the day of the lesson in string format (dd MMMM yyyy).
     *
     * @return the lesson day
     */
    public String getDay(){
        return new SimpleDateFormat("dd MMMM yyyy").format(this.startDate);
    }

    /**
     * Get the minutes lesson duration.
     *
     * @return the minutes lesson duration
     */
    public int getMinutesDuration(){
        Calendar startDate = Calendar.getInstance();
        startDate.setTime(this.startDate);
        Calendar endDate = Calendar.getInstance();
        endDate.setTime(this.endDate);
        return (int)TimeUnit.MILLISECONDS.toMinutes(endDate.getTimeInMillis() - startDate.getTimeInMillis());
    }

    /**
     * Get the time remaining before the start date lesson in string format {@link Lesson#formattedTimeLeft(long)}.
     *
     * @return the time remaining before the start date lesson
     */
    public String timeRemaining() {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(this.startDate);
        return formattedTimeLeft(calendar.getTimeInMillis() - System.currentTimeMillis());
    }


    /**
     * Get the time remaining before the start date lesson in seconds.
     *
     * @return the time remaining before the start date lesson in seconds.
     */
    public int timeRemainingInSeconds() {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(this.startDate);
        return (int)TimeUnit.MILLISECONDS.toSeconds(calendar.getTimeInMillis() - System.currentTimeMillis());
    }

    /**
     * Check if this lesson and the one passed in parameter are the same.
     *
     * <br> Same UID, same start date, same end date and same lesson name.
     *
     * @param lesson the lesson to compare
     * @return true if the lesson are the same, false otherwise.
     */
    public boolean sameLessonsAndSameDate(Lesson lesson) {
        if(lesson.getUID().equals(this.UID))
            if (lesson.getStartDate().equals(this.startDate))
                if(lesson.getEndDate().equals(this.endDate))
                    return (lesson.getName().equals(this.name));

        return false;

    }

    /**
     * Compare the start dates with the lesson passed in parameter.
     *
     * @param aLesson the lesson to compare start date
     * @return  the value {@code 0} if the start date of the lesson passed in parameter is equal to
     *          this start date; a value less than {@code 0} if this start date
     *          is before the start date of the lesson passed in parameter; and a value greater than
     *      {@code 0} if this start date is after the start date of the lesson passed in parameter.
     */
    @Override
    public int compareTo(@NotNull Lesson aLesson) {
        Date aDate = aLesson.getStartDate();
        return this.startDate.compareTo(aDate);
    }

    /**
     * Check if UID are the same with the lesson passed in parameter.
     *
     * @param lesson the lesson to compare UID
     * @return true if the lessons have the same UID, false otherwise
     */
    @Override
    public boolean equals(Object lesson){
        if(lesson instanceof Lesson)
            return ((Lesson) lesson).getUID().equals(this.getUID());

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Lesson[ name=" + this.name + ", startDate=" + this.startDate + ", endDate=" + this.endDate +
                ", room=" + this.room + " ]";
    }

    /**
     * Convert milliseconds to "x day (s) y hour (s) z minute (s)".
     *
     * @param millis the milliseconds to convert
     * @return the time to "x day (s) y hour (s) z minute (s)"
     */
    private static String formattedTimeLeft(final long millis) {
        int days = (int)(TimeUnit.MILLISECONDS.toDays(millis));
        int hrs = (int)(TimeUnit.MILLISECONDS.toHours(millis) % 24L);
        int min = (int)(TimeUnit.MILLISECONDS.toMinutes(millis) % 60L);

        if((int)(TimeUnit.MILLISECONDS.toSeconds(millis) % 60L) > 0){
            min+=1;
            if(min == 60){
                hrs+=1;
                min=0;
                if(hrs == 24){
                    days+=1;
                    hrs=0;
                }
            }
        }

        String res = "";
        if(days == 1)
            res += "1 jour ";
        else if(days > 1)
            res += days + " jours ";

        if(hrs == 1)
            res += "1 heure ";
        else if(hrs > 1)
            res += hrs + " heures ";

        if(min == 1)
            res += "1 minute ";
        else if(min > 1)
            res += min + " minutes ";

        return res;


    }

}

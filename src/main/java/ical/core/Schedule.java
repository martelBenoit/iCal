package ical.core;

import ical.database.entity.Lesson;
import ical.database.entity.MovedLesson;
import ical.database.entity.Professor;
import ical.util.Tools;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Schedule class.
 *
 * <br>This class stores all the lessons for a timetable.
 *
 * @author Benoît Martel
 * @version 1.4
 * @since 1.0
 */
public class Schedule extends AbstractSchedule {

    /**
     * the previous lessons list.
     */
    private ArrayList<Lesson> previousLessons = new ArrayList<>();

    /**
     * notification indicator.
     */
    private boolean alreadyBeenNotified;

    /**
     * Default constructor.
     *
     * <br>Initializes a new empty schedule.
     */
    public Schedule(){
        super();
    }

    /**
     * Constructor.
     *
     * <br>Initializes a new schedule with its url.
     *
     * @param url the url of the schedule
     */
    public Schedule(String url){

        super();

        if(url != null && !url.equals("")){

            try{
                this.url = new URL(url);
                net.fortuna.ical4j.model.Calendar calendar = new CalendarBuilder().build(this.url.openStream());
                fillSchedule(calendar);
                creationDate = Instant.ofEpochMilli(System.currentTimeMillis());
                this.previousLessons = this.lessons;
            } catch (ParseException | IOException exception){
                LOGGER.error(exception.getMessage(),exception);
            } catch (ParserException parser){
                LOGGER.error("Parser error : "+parser.getMessage());
            }
        }

        this.alreadyBeenNotified = false;

    }

    /**
     * Update the schedule.
     *
     * @throws IOException if cannot open the url
     * @throws ParseException if cannot parse the url
     * @throws ParserException if cannot parse the ics file
     */
    public void updateEntries() throws IOException, ParseException, ParserException {

        // On vérfie que l'url n'est pas null
        if(this.url != null){
            this.previousLessons = this.lessons;

            net.fortuna.ical4j.model.Calendar calendar = new CalendarBuilder().build(this.url.openStream());
            fillSchedule(calendar);
            creationDate = Instant.ofEpochMilli(System.currentTimeMillis());

        }

    }

    /**
     * Set the url schedule.
     *
     * @param url the new url schedule
     */
    public void setURL(URL url) {
        this.url = url;
        try{
            net.fortuna.ical4j.model.Calendar calendar = new CalendarBuilder().build(this.url.openStream());
            fillSchedule(calendar);
            this.previousLessons = this.lessons;
            creationDate = Instant.ofEpochMilli(System.currentTimeMillis());
        } catch(ParseException | IOException | ParserException e){
            LOGGER.error(e.getMessage());
        }

    }

    /**
     * Get the list of the added lessons.
     *
     * @return the list of the added lessons
     */
    public ArrayList<MovedLesson> getAddedLessons(){
        ArrayList<MovedLesson> addedLessons = new ArrayList<>();
        for(Lesson lesson : this.lessons)
            if(!previousLessons.contains(lesson))
                if(Tools.verifyWatchUp(lesson))
                    addedLessons.add(new MovedLesson(null,lesson));

        return addedLessons;
    }

    /**
     * Get the list of the removed lessons.
     *
     * @return the list of the removed lessons
     */
    public ArrayList<MovedLesson> getRemovedLessons(){
        ArrayList<MovedLesson> removedLessons = new ArrayList<>();
        for(Lesson lesson : previousLessons)
            if(!this.lessons.contains(lesson))
                if(Tools.verifyWatchUp(lesson))
                    removedLessons.add(new MovedLesson(lesson,null));

        return removedLessons;
    }

    /**
     * Get the list of the moved lessons.
     *
     * @return the list of the moved lessons
     */
    public List<MovedLesson> getMovedLessons(){

        ArrayList<MovedLesson> movedLessons = new ArrayList<>();
        for(Lesson aPreviousLesson : previousLessons){
            for(Lesson anActualLesson : lessons){
                if(aPreviousLesson.equals(anActualLesson))
                    if(!aPreviousLesson.sameLessonsAndSameDate(anActualLesson))
                        if(Tools.verifyWatchUp(aPreviousLesson))
                            movedLessons.add(new MovedLesson(aPreviousLesson,anActualLesson));
            }
        }

        return movedLessons;
    }

    /**
     * Get the list of lessons given in x days.
     *
     * @param toDayNumber the x day(s) number
     * @return the list of lessons given in x days
     */
    public ArrayList<Lesson> getLessons(final int toDayNumber) {
        final Date dateToCompare = Tools.addDaysToTodayDate(toDayNumber);
        final ArrayList<Lesson> lessonsDays = new ArrayList<>();
        for (final Lesson lesson : this.lessons) {
            if (Tools.getDateWithTimeToZero(lesson.getStartDate()).compareTo(dateToCompare) == 0) {
                lessonsDays.add(lesson);
            }
        }
        return lessonsDays;
    }

    /**
     * Get the next lessons.
     *
     * <br>If the next lesson takes place at the same time as another lesson (same time, same date) then the method
     * returns all the lessons.
     *
     * @return the next lessons
     */
    public ArrayList<Lesson> getNextLessons(){

        int i = 0;
        boolean found = false;
        Lesson firstLesson = null;
        ArrayList<Lesson> ret = new ArrayList<>();

        final Date currentDate = new Date();
        while (i < this.lessons.size() && !found) {
            if (this.lessons.get(i).getStartDate().compareTo(currentDate) >= 0) {
                firstLesson = this.lessons.get(i);
                found = true;
            }
            i++;
        }

        if(found){
            ret.add(firstLesson);
            while(i < this.lessons.size()){
                if(this.lessons.get(i).getStartDate().equals(firstLesson.getStartDate()))
                    ret.add(this.lessons.get(i));
                i++;
            }
        }
        return ret;

    }

    @Nullable
    public Lesson getNextLesson(@NotNull Professor professor){

        Lesson ret = null;
        boolean found = false;

        final Date currentDate = new Date();
        int i = 0;
        while (i < this.lessons.size() && !found) {
            if (this.lessons.get(i).getStartDate().compareTo(currentDate) >= 0) {
                if(this.lessons.get(i).getProfessor().getName().equals(professor.getName())) {
                    ret = this.lessons.get(i);
                    found = true;
                }
            }
            i++;
        }
        return ret;
    }

    /**
     * Get the list of lessons for the week.
     *
     * @param weekNumber the week number
     * @return the list of lessons for the week
     */
    public ArrayList<Lesson> getWeekLessons(final int weekNumber){

        ArrayList<Lesson> res = new ArrayList<>();

        Calendar cal = Calendar.getInstance();

        for(Lesson lesson : lessons){
            cal.setTime(lesson.getStartDate());
            if(cal.get(Calendar.WEEK_OF_YEAR) == weekNumber)
                res.add(lesson);

        }

        return res;

    }

    /**
     * Get the list of lessons for the week.
     *
     * @return the list of lessons for the week
     */
    public ArrayList<Lesson> getWeekLessons(){

        ArrayList<Lesson> res = new ArrayList<>();

        Calendar actual = Calendar.getInstance();
        actual.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        actual.set(Calendar.HOUR,0);
        actual.set(Calendar.MINUTE,0);
        actual.set(Calendar.SECOND,0);
        actual.set(Calendar.MILLISECOND,0);

        Calendar cal = Calendar.getInstance();

        for(Lesson lesson : lessons){
            cal.setTime(lesson.getStartDate());
            int day = (int)(TimeUnit.MILLISECONDS.toDays(cal.getTimeInMillis()-actual.getTimeInMillis()));

            if(day < 6 && day >= 0) {
                res.add(lesson);
            }
        }

        return res;

    }

    /**
     * Get the list of lessons for next week.
     *
     * @return the list of lessons for next week
     */
    public ArrayList<Lesson> getNextWeekLessons(){

        ArrayList<Lesson> res = new ArrayList<>();

        Calendar actual = Calendar.getInstance();
        actual.add(Calendar.DATE,7);
        actual.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        actual.set(Calendar.HOUR,0);
        actual.set(Calendar.MINUTE,0);
        actual.set(Calendar.SECOND,0);
        actual.set(Calendar.MILLISECOND,0);

        Calendar cal = Calendar.getInstance();

        for(Lesson lesson : lessons){
            cal.setTime(lesson.getStartDate());
            int day = (int)(TimeUnit.MILLISECONDS.toDays(cal.getTimeInMillis()-actual.getTimeInMillis()));

            if(day < 6 && day >= 0) {
                res.add(lesson);
            }
        }

        return res;

    }

    /**
     * Get actual lessons.
     *
     * @return the list of actual lessons
     */
    public ArrayList<Lesson> getActualLessons(){

        ArrayList<Lesson> ret = new ArrayList<>();

        final Date currentDate = new Date();

        for(Lesson lesson : this.lessons){
            if(lesson.getStartDate().compareTo(currentDate) <= 0 && lesson.getEndDate().compareTo(currentDate) >= 0)
                ret.add(lesson);
        }

        return ret;
    }

    /**
     * Retrieve all the lessons scheduled with the professor passed as a parameter
     * @param professor the professor
     * @return the lessons list with the professor
     */
    public List<Lesson> getLessonWithProfessor(Professor professor){
        if(professor != null && professor.getDisplayName() != null){
            return lessons.stream().filter(l -> l.getProfessor().getDisplayName().equals(professor.getDisplayName()) && l.timeRemainingInSeconds() > 0).collect(Collectors.toList());
        }
        else return new ArrayList<>();
    }

    /**
     * Check if the guild has already been notified.
     *
     * @return true if the guild has already been notified, else otherwise
     */
    public boolean hasAlreadyBeenNotified(){
        return alreadyBeenNotified;
    }

    /**
     * Set the notification indicator.
     *
     * @param notified true if the guild has just been notified, else otherwise
     */
    public void setNotified(boolean notified){
        this.alreadyBeenNotified = notified;
    }

    /**
     * Reset previous lessons with the actual lessons.
     */
    public void resetPreviousLessons(){
        this.previousLessons = this.lessons;
    }


}

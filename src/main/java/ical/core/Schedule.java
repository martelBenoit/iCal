package ical.core;

import ical.database.entity.Lesson;
import ical.database.entity.MovedLesson;
import ical.util.Tools;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Schedule extends AbstractSchedule {


    private ArrayList<Lesson> previousLessons = new ArrayList<>();

    private boolean alreadyBeenNotified;

    public Schedule(){
        super();
    }


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

    public void updateEntries() throws IOException, ParseException, ParserException {

        // On vérfie que l'url n'est pas null
        if(this.url != null){
            this.previousLessons = this.lessons;

            net.fortuna.ical4j.model.Calendar calendar = new CalendarBuilder().build(this.url.openStream());
            fillSchedule(calendar);
            creationDate = Instant.ofEpochMilli(System.currentTimeMillis());

        }


    }

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

    public ArrayList<MovedLesson> getAddedLessons(){
        ArrayList<MovedLesson> addedLessons = new ArrayList<>();
        for(Lesson lesson : this.lessons)
            if(!previousLessons.contains(lesson))
                if(verifyWatchUp(lesson))
                    addedLessons.add(new MovedLesson(null,lesson));

        return addedLessons;
    }

    public ArrayList<MovedLesson> getRemovedLessons(){
        ArrayList<MovedLesson> removedLessons = new ArrayList<>();
        for(Lesson lesson : previousLessons)
            if(!this.lessons.contains(lesson))
                if(verifyWatchUp(lesson))
                    removedLessons.add(new MovedLesson(lesson,null));

        return removedLessons;
    }

    public List<MovedLesson> getMovedLessons(){

        ArrayList<MovedLesson> movedLessons = new ArrayList<>();
        for(Lesson aPreviousLesson : previousLessons){
            for(Lesson anActualLesson : lessons){
                if(aPreviousLesson.equals(anActualLesson))
                    if(!aPreviousLesson.sameLessonsAndSameDate(anActualLesson))
                        if(verifyWatchUp(aPreviousLesson))
                            movedLessons.add(new MovedLesson(aPreviousLesson,anActualLesson));
            }
        }

        return movedLessons;
    }

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
     * Get actual lessons
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

    public boolean verifyWatchUp(@NotNull Lesson lesson){

        boolean res = false;

        if(lesson.getStartDate().getTime() >= System.currentTimeMillis()){
            Calendar cal = Calendar.getInstance();
            cal.setTime(lesson.getStartDate());

            Calendar actual = Calendar.getInstance();
            actual.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

            int days = (int)(TimeUnit.MILLISECONDS.toDays(cal.getTimeInMillis()-actual.getTimeInMillis()));
            if(days <= 14)
                res = true;
        }

        return res;
    }


    public boolean hasAlreadyBeenNotified(){
        return alreadyBeenNotified;
    }

    public void setNotified(boolean notified){
        this.alreadyBeenNotified = notified;
    }

    public void resetPreviousLessons(){
        this.previousLessons = this.lessons;
    }

}

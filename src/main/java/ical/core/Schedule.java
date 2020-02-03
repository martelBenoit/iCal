package ical.core;

import ical.database.DAOFactory;
import ical.database.dao.ProfessorDAO;
import ical.database.entity.Lesson;
import ical.database.entity.MovedLesson;
import ical.database.entity.Professor;
import ical.util.Tools;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.TemporalAccessor;
import java.util.*;

public class Schedule {

    private static final Logger LOGGER = LoggerFactory.getLogger(Schedule.class);

    private URL url;

    private TemporalAccessor creationDate;

    private ArrayList<Lesson> lessons = new ArrayList<>();

    private ArrayList<Lesson> previousLessons = new ArrayList<>();

    private final SimpleDateFormat dateFormat;

    private boolean alreadyBeenNotified;

    public Schedule(String url){

        this.alreadyBeenNotified = false;

        dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        if(url != null && !url.equals("")){
            try{
                this.url = new URL(url);
                Calendar calendar = new CalendarBuilder().build(this.url.openStream());
                fillSchedule(calendar);
                creationDate = Instant.ofEpochMilli(System.currentTimeMillis());
                this.previousLessons = this.lessons;
            } catch (ParseException | ParserException | IOException exception){
                LOGGER.error(exception.getMessage(),exception);

            }
        }

    }

    public void updateLessons() throws IOException, ParserException, ParseException {

        // On vérfie que l'url n'est pas null
        if(this.url != null){
            this.previousLessons = this.lessons;

            Calendar calendar = new CalendarBuilder().build(this.url.openStream());
            fillSchedule(calendar);
            creationDate = Instant.ofEpochMilli(System.currentTimeMillis());
        }

    }

    public void setURL(URL url) {
        this.url = url;
        try{
            Calendar calendar = new CalendarBuilder().build(this.url.openStream());
            fillSchedule(calendar);
            this.previousLessons = this.lessons;
            creationDate = Instant.ofEpochMilli(System.currentTimeMillis());
        } catch(ParseException | IOException | ParserException e){
            LOGGER.error(e.getMessage());
        }

    }

    private void fillSchedule(Calendar calendar) throws ParseException {

        this.lessons = new ArrayList<>();

        for (Component cours : calendar.getComponents("VEVENT")) {
            String name = cours.getProperty("SUMMARY").getValue();
            Date dtstart = dateFormat.parse(cours.getProperty("DTSTART").getValue());
            Date dtend = dateFormat.parse(cours.getProperty("DTEND").getValue());
            String description = cours.getProperty("DESCRIPTION").getValue();
            String uid = cours.getProperty("UID").getValue();

            // On récupère les informations utiles contenues dans le champs description.
            int index = description.indexOf("Export\u00e9");
            description = description.substring(1, index - 2);
            String[] decompose = description.split("[\\r\\n|\\r|\\n]+");

            // On récupère le nom du prof
            String profName = (decompose[decompose.length - 1].replaceAll("[\\s|^\\W]", "")).replaceAll("\\.","");

            // On récupère les groupes
            description = "";
            for(int i = 0; i < decompose.length-1; i++)
                if(i != decompose.length-2)
                    description = description.concat(decompose[i]+", ");
                else
                    description = description.concat(decompose[i].replaceAll("\\.",""));

            description = description.concat("\n"+decompose[decompose.length-1]);

            String room = cours.getProperty("LOCATION").getValue();

            ProfessorDAO professorDAO = (ProfessorDAO) DAOFactory.getProfessorDAO();
            Professor professor = professorDAO.find(profName);
            if(professor == null){
                professor = professorDAO.create(new Professor(profName));
                LOGGER.info("Add new prof in database : "+professor);
            }

            if(professor != null)
                this.lessons.add(new Lesson(uid, name, dtstart, dtend, description, professor, room));
            else
                LOGGER.info("Lesson not added");
        }

        Collections.sort(this.lessons);

    }

    public ArrayList<MovedLesson> getAddedLessons(){
        ArrayList<MovedLesson> addedLessons = new ArrayList<>();
        for(Lesson lesson : this.lessons)
            if(!previousLessons.contains(lesson))
                addedLessons.add(new MovedLesson(null,lesson));



        return addedLessons;
    }

    public ArrayList<MovedLesson> getRemovedLessons(){
        ArrayList<MovedLesson> removedLessons = new ArrayList<>();
        for(Lesson lesson : previousLessons)
            if(!this.lessons.contains(lesson))
                removedLessons.add(new MovedLesson(lesson,null));

        return removedLessons;
    }

    public List<MovedLesson> getMovedLessons(){

        ArrayList<MovedLesson> movedLessons = new ArrayList<>();
        for(Lesson aPreviousLesson : previousLessons){
            for(Lesson anActualLesson : lessons){
                if(aPreviousLesson.equals(anActualLesson))
                    if(!aPreviousLesson.sameLessonsAndSameDate(anActualLesson))
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


    public TemporalAccessor getCreationDate(){
        return this.creationDate;
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

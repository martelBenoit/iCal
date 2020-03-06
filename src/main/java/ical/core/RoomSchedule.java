package ical.core;

import ical.database.entity.Lesson;
import ical.util.Config;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;

public class RoomSchedule extends AbstractSchedule {

    public RoomSchedule(){

        super();

        String url = Config.get("ROOM_SCHEDULE");

        if(url != null && !url.equals("")){

            try{
                this.url = new URL(url);
                net.fortuna.ical4j.model.Calendar calendar = new CalendarBuilder().build(this.url.openStream());
                fillSchedule(calendar);
                creationDate = Instant.ofEpochMilli(System.currentTimeMillis());
            } catch (ParseException | IOException exception){
                LOGGER.error(exception.getMessage(),exception);
            } catch (ParserException parser){
                LOGGER.error("Parser error : "+parser.getMessage());
            }
        }

    }


    @Override
    public void updateLessons() throws IOException, ParseException, ParserException {

        // On vérfie que l'url n'est pas null
        if(this.url != null){

            net.fortuna.ical4j.model.Calendar calendar = new CalendarBuilder().build(this.url.openStream());
            fillSchedule(calendar);
            creationDate = Instant.ofEpochMilli(System.currentTimeMillis());

        }

    }

    public ArrayList<Lesson> getNowLesson(){

        Date now = new Date();

        ArrayList<Lesson> res = new ArrayList<>();

        for(Lesson lesson : this.lessons){

            if(lesson.getStartDate().compareTo(now) < 0 && lesson.getEndDate().compareTo(now) > 0){
                res.add(lesson);
            }

        }


        return res;


    }

    public ArrayList<Lesson> getLessonsAt(Date date){

        ArrayList<Lesson> res = new ArrayList<>();

        for(Lesson lesson : this.lessons){

            if(lesson.getStartDate().compareTo(date) < 0 && lesson.getEndDate().compareTo(date) > 0){
                res.add(lesson);
            }

        }


        return res;


    }


}

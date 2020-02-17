package ical.core;

import ical.database.DAOFactory;
import ical.database.dao.ProfessorDAO;
import ical.database.entity.Lesson;
import ical.database.entity.Professor;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.temporal.TemporalAccessor;
import java.util.*;

public abstract class AbstractSchedule {

    protected static final Logger LOGGER = LoggerFactory.getLogger(Schedule.class);

    protected TemporalAccessor creationDate;

    protected ArrayList<Lesson> lessons = new ArrayList<>();

    protected final SimpleDateFormat dateFormat;

    protected URL url;



    public AbstractSchedule(){


        dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));


    }



    public abstract void updateLessons() throws IOException, ParseException, ParserException;



    protected void fillSchedule(Calendar calendar) throws ParseException {

        this.lessons = new ArrayList<>();

        for (Component cours : calendar.getComponents("VEVENT")) {
            String name = cours.getProperty("SUMMARY").getValue();
            Date dtstart = dateFormat.parse(cours.getProperty("DTSTART").getValue());
            Date dtend = dateFormat.parse(cours.getProperty("DTEND").getValue());
            String description = cours.getProperty("DESCRIPTION").getValue();
            String uid = cours.getProperty("UID").getValue();
            String room = cours.getProperty("LOCATION").getValue();

            // On récupère les informations utiles contenues dans le champs description.
            int index = description.indexOf("Export\u00e9");
            description = description.substring(1, index - 2);
            String[] decompose = description.split("[\\r\\n|\\r|\\n]+");

            if(decompose.length >= 1) {
                String profName = (decompose[decompose.length - 1].replaceAll("[\\s|^\\W]", "")).replaceAll("\\.", "");


                // On récupère les groupes
                description = "";
                for (int i = 0; i < decompose.length - 1; i++)
                    if (i != decompose.length - 2)
                        if (!decompose[i].equals(""))
                            description = description.concat(decompose[i] + ", ");
                        else
                            description = description.concat(decompose[i].replaceAll("\\.", ""));

                description = description.concat("\n" + decompose[decompose.length - 1]);



                ProfessorDAO professorDAO = (ProfessorDAO) DAOFactory.getProfessorDAO();
                Professor professor = professorDAO.find(profName);
                if (professor == null) {
                    professor = professorDAO.create(new Professor(profName));
                    LOGGER.info("Add new prof in database : " + professor);
                }

                if (professor != null)
                    this.lessons.add(new Lesson(uid, name, dtstart, dtend, description, professor, room));
                else
                    LOGGER.info("Lesson not added");
            }
            else{

                this.lessons.add(new Lesson(uid,name,dtstart,dtend,description,null,room));
                LOGGER.info("Lesson added with no description : "+name);
            }
        }

        Collections.sort(this.lessons);

    }


    public TemporalAccessor getCreationDate(){
        return this.creationDate;
    }

}

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

/**
 * Abstract schedule class.
 *
 * @author Benoît Martel
 * @version 1.0
 * @since 1.0
 */
public abstract class AbstractSchedule {

    /**
     * the logger.
     */
    protected static final Logger LOGGER = LoggerFactory.getLogger(Schedule.class);

    /**
     * the schedule creation date.
     */
    protected TemporalAccessor creationDate;

    /**
     * the lessons list.
     */
    protected ArrayList<Lesson> lessons = new ArrayList<>();

    /**
     * the date format.
     */
    protected final SimpleDateFormat dateFormat;

    /**
     * the schedule url.
     */
    protected URL url;

    /**
     * Default constructor.
     */
    public AbstractSchedule(){

        dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

    }

    /**
     * Update schedule entries.
     *
     * @throws IOException if cannot open the url
     * @throws ParseException if cannot parse the url
     * @throws ParserException if cannot parse the ics file
     */
    public abstract void updateEntries() throws IOException, ParseException, ParserException;

    /**
     * Reset and fill the schedule with the calendar object.
     *
     * @param calendar the calendar containing the result of parsing the ics file
     * @throws ParseException if cannot parse the calendar object
     */
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
                String profName = (
                        decompose[decompose.length - 1]
                        .replaceAll("[\\s|^\\W]", "")
                ).replaceAll("\\.", "");

                ProfessorDAO professorDAO = (ProfessorDAO) DAOFactory.getProfessorDAO();
                Professor professor = professorDAO.find(profName);
                if (professor == null) {
                    professor = professorDAO.create(new Professor(profName,decompose[decompose.length-1]));
                    LOGGER.info("Add new prof in database : " + professor);
                }

                if (professor != null){
                    professor.setDisplayName(decompose[decompose.length-1]);
                    professorDAO.update(professor);
                    this.lessons.add(new Lesson(uid, name, dtstart, dtend, description, professor, room));
                }
                else {
                    LOGGER.info("Lesson added but error with the professor ");
                    this.lessons.add(new Lesson(uid, name, dtstart, dtend, description, null, room));
                }
            }
            else
                this.lessons.add(new Lesson(uid,name,dtstart,dtend,description,null,room));

        }

        Collections.sort(this.lessons);

    }

    /**
     * Get the schedule creation date.
     *
     * @return the creation date
     */
    public TemporalAccessor getCreationDate(){
        return this.creationDate;
    }

    /**
     * Get lessons
     *
     * @return the lessons
     */
    public ArrayList<Lesson> getLessons(){
        return lessons;
    }

}

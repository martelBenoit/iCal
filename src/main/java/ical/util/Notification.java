package ical.util;

import ical.database.dao.EventChangeDAO;
import ical.database.dao.EventChange_LessonDAO;
import ical.database.dao.MovedLessonDAO;
import ical.database.entity.EventChange_Lesson;
import ical.database.entity.Lesson;
import ical.database.DAOFactory;
import ical.database.dao.LessonDAO;
import ical.database.entity.MovedLesson;
import ical.database.entity.OEventChange;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.time.temporal.TemporalAccessor;
import java.util.List;

public class Notification {

    private static final Logger LOGGER = LoggerFactory.getLogger(Notification.class);

    public static MessageEmbed prepareNotificationNextLessons(List<Lesson> lessons){

        MessageEmbed me = null;

        if(lessons == null)
            throw new NullPointerException("Parameter lessons is null");

        if(lessons.size() >= 1){

            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(new Color(238358));

            if(lessons.size() == 1){
                eb.setTitle("  " + lessons.get(0).getName(), null);
                eb.setThumbnail(lessons.get(0).getProfessor().getUrl());
                eb.addField("Date", lessons.get(0).getDay(), false);
                eb.addField("Début du cours", lessons.get(0).getStartTime(), true);
                eb.addField("Fin du cours", lessons.get(0).getEndTime(), true);
                eb.addField("Salle", lessons.get(0).getRoom(), false);
                eb.addField("Plus d'infos", lessons.get(0).getDescription(), false);
                me = eb.build();
            }
            else {
                eb.setTitle("  Plusieurs cours à venir", null);
                eb.addField("Date", lessons.get(0).getDay(), false);
                eb.addField("Début des cours", lessons.get(0).getStartTime(), false);
                for(Lesson lesson : lessons){
                    String description = "Fin du cours : "+lesson.getEndTime()+"\n"+
                            "Salle : "+lesson.getRoom()+"\n"+
                            "Plus d'infos : "+lesson.getDescription();
                    eb.addField(lesson.getName(), description, true);
                }
                me = eb.build();
            }
        }

        return me;
    }

    public static MessageEmbed prepareNotificationModificationsLessons(OEventChange eventChange, List<MovedLesson> movedLessons, TemporalAccessor time){

        EventChangeDAO evtChangeDAO = (EventChangeDAO) DAOFactory.getEventChange();
        OEventChange evtChange = evtChangeDAO.create(eventChange);

        ModificationType typeEvent = eventChange.getType();

        if(evtChange != null) {
            System.out.println("event change created");
            System.out.println(typeEvent);
            EventChange_LessonDAO eventChangeLessonDAO = (EventChange_LessonDAO) DAOFactory.getEventChange_Lesson();
            MovedLessonDAO movedLessonDAO = (MovedLessonDAO) DAOFactory.getMovedLessonDAO();
            LessonDAO lessonDAO = (LessonDAO) DAOFactory.getLessonDAO();



            // Si on enregistre un déplacement de cours
            if(typeEvent == ModificationType.MOVE){

                for (MovedLesson movedLesson : movedLessons){
                    if(movedLesson.getPreviousLesson() != null && movedLesson.getActualLesson() != null){
                        Lesson prevLesson = lessonDAO.create(movedLesson.getPreviousLesson());
                        Lesson actLesson = lessonDAO.create(movedLesson.getActualLesson());
                        movedLesson.setPreviousLesson(prevLesson);
                        movedLesson.setActualLesson(actLesson);
                        MovedLesson movedLessonCreated = movedLessonDAO.create(movedLesson);
                        if(movedLessonCreated != null)
                            eventChangeLessonDAO.create(new EventChange_Lesson(evtChange,movedLessonCreated));
                        else
                            LOGGER.error("Error movedLessonCreated is null");
                    }
                    else
                        LOGGER.error("To create an moved event change lesson you should insert a prevLesson and a actLesson not null ");

                }

            }
            else if(typeEvent == ModificationType.ADD){

                for (MovedLesson movedLesson : movedLessons){

                    Lesson actLesson = lessonDAO.create(movedLesson.getActualLesson());
                    movedLesson.setActualLesson(actLesson);
                    MovedLesson movedLessonCreated = movedLessonDAO.create(movedLesson);
                    eventChangeLessonDAO.create(new EventChange_Lesson(evtChange,movedLessonCreated));
                }

            }
            else if(typeEvent == ModificationType.REMOVE){

                for (MovedLesson movedLesson : movedLessons){
                    Lesson prevLesson = lessonDAO.create(movedLesson.getPreviousLesson());
                    movedLesson.setPreviousLesson(prevLesson);
                    MovedLesson movedLessonCreated = movedLessonDAO.create(movedLesson);
                    eventChangeLessonDAO.create(new EventChange_Lesson(evtChange,movedLessonCreated));
                }

            }

        }

        String dynamicString = "";
        if(ModificationType.ADD == typeEvent)
            dynamicString = "ajouté";
        else if(ModificationType.REMOVE == typeEvent)
            dynamicString = "supprimé";
        else if(ModificationType.MOVE == typeEvent)
            dynamicString = "déplacé";

        if(movedLessons.size() > 1)
            dynamicString = dynamicString.concat("s");

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("[Information] - Changement d'emploi du temps", "https://ical.benoitmartel.ovh/?id="+eventChange.getId());
        eb.setTimestamp(time);
        eb.setFooter("ENT","https://www-ensibs.univ-ubs.fr/skins/ENSIBS/resources/img/logo.png");
        eb.setColor(new Color(238358));
        if(movedLessons.size() == 1) {
            eb.setDescription("Il y a un cours qui a été " + dynamicString + " !");
            Lesson newLesson;
            Lesson oldLesson;
            if (typeEvent == ModificationType.MOVE){
                for (MovedLesson movedLesson : movedLessons) {
                    oldLesson = movedLesson.getPreviousLesson();
                    newLesson = movedLesson.getActualLesson();
                    eb.addField("", newLesson.getName() + "\n~~Le " + oldLesson.getDay()+"~~\nLe "+newLesson.getDay(), false);
                    eb.addField("*D\u00e9but du cours*", "~~"+oldLesson.getStartTime()+"~~\n"+newLesson.getStartTime(), true);
                    eb.addField("*Fin du cours*", "~~"+oldLesson.getEndTime()+"~~\n"+newLesson.getEndTime(), true);
                    eb.addBlankField(false);
                }
            }
            else{
                for (MovedLesson movedLesson : movedLessons) {
                    if (movedLesson.getPreviousLesson() != null)
                        newLesson = movedLesson.getPreviousLesson();
                    else
                        newLesson = movedLesson.getActualLesson();
                    eb.addField("", newLesson.getName() + "\nLe " + newLesson.getDay(), false);
                    eb.addField("*D\u00e9but du cours*", newLesson.getStartTime(), true);
                    eb.addField("*Fin du cours*", newLesson.getEndTime(), true);
                    eb.addBlankField(false);
                }
            }
        }
        else if(movedLessons.size() <= 3) {
            eb.setDescription("Il y a " + movedLessons.size() + " cours qui ont été "+dynamicString+" !");
            Lesson newLesson;
            Lesson oldLesson;
            if (typeEvent == ModificationType.MOVE){
                for (MovedLesson movedLesson : movedLessons) {
                    oldLesson = movedLesson.getPreviousLesson();
                    newLesson = movedLesson.getActualLesson();
                    eb.addField("", newLesson.getName() + "\n~~Le " + oldLesson.getDay()+"~~\nLe "+newLesson.getDay(), false);
                    eb.addField("*D\u00e9but du cours*", "~~"+oldLesson.getStartTime()+"~~\n"+newLesson.getStartTime(), true);
                    eb.addField("*Fin du cours*", "~~"+oldLesson.getEndTime()+"~~\n"+newLesson.getEndTime(), true);
                    eb.addBlankField(false);
                }
            }
            else {
                for (MovedLesson movedLesson : movedLessons) {
                    if (movedLesson.getPreviousLesson() != null)
                        newLesson = movedLesson.getPreviousLesson();
                    else
                        newLesson = movedLesson.getActualLesson();
                    eb.addField("", newLesson.getName() + "\nLe " + newLesson.getDay(), false);
                    eb.addField("*D\u00e9but du cours*", newLesson.getStartTime(), true);
                    eb.addField("*Fin du cours*", newLesson.getEndTime(), true);
                    eb.addBlankField(false);
                }
            }
        }
        else{
            eb.setDescription("Il y a "+movedLessons.size()+" cours qui ont été "+dynamicString+" !\nVoici les 3 premiers : ");
            Lesson newLesson;
            Lesson oldLesson;
            if (typeEvent == ModificationType.MOVE){
                for (MovedLesson movedLesson : movedLessons) {
                    oldLesson = movedLesson.getPreviousLesson();
                    newLesson = movedLesson.getActualLesson();
                    eb.addField("", newLesson.getName() + "\n~~Le " + oldLesson.getDay()+"~~\nLe "+newLesson.getDay(), false);
                    eb.addField("*D\u00e9but du cours*", "~~"+oldLesson.getStartTime()+"~~\n"+newLesson.getStartTime(), true);
                    eb.addField("*Fin du cours*", "~~"+oldLesson.getEndTime()+"~~\n"+newLesson.getEndTime(), true);
                    eb.addBlankField(false);
                }
            }
            else {
                for (MovedLesson movedLesson : movedLessons.subList(0, 3)) {
                    if (movedLesson.getPreviousLesson() != null)
                        newLesson = movedLesson.getPreviousLesson();
                    else
                        newLesson = movedLesson.getActualLesson();
                    eb.addField("", newLesson.getName() + "\nLe " + newLesson.getDay(), false);
                    eb.addField("*D\u00e9but du cours*", newLesson.getStartTime(), true);
                    eb.addField("*Fin du cours*", newLesson.getEndTime(), true);
                    eb.addBlankField(false);
                }
            }
        }

        return eb.build();
    }
}

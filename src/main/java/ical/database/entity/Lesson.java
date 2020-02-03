package ical.database.entity;

import ical.util.Config;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

public class Lesson implements Comparable<Lesson>{

    private final String UID;

    private String uniqueID = null;

    private String name;

    private Date startDate;
    private Date endDate;

    private String description;
    private Professor professor;

    private String room;

    public Lesson(String uid, String uniqueID, String name, Date startDate, Date endDate, String description, Professor professor, String room){
        this.UID = uid;
        this.uniqueID = uniqueID;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
        this.professor = professor;
        this.room = room;
    }

    public Lesson(String uid, String name, Date startDate, Date endDate, String description, Professor professor, String room){
        this.UID = uid;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
        this.professor = professor;
        this.room = room;
    }

    /*public void setDate(){
        java.util.Calendar c = new GregorianCalendar();
        c.add(java.util.Calendar.MINUTE,5);
        this.startDate = c.getTime();
    }*/

    public String getUID(){
        return this.UID;
    }

    public String getUniqueID(){
        return this.uniqueID;
    }

    public void setUniqueID(String uniqueID){
        this.uniqueID = uniqueID;
    }

    public String getName(){
        return this.name;
    }

    public Date getStartDate(){
        return this.startDate;
    }

    public Date getEndDate(){
        return this.endDate;
    }

    public String getDescription(){
        return this.description;
    }

    public Professor getProfessor(){
        return this.professor;
    }

    public void setProfessor(Professor professor){
        this.professor = professor;
    }

    public String getRoom(){
        return this.room;
    }

    public String getStartTime(){
        Calendar cal = Calendar.getInstance();
        cal.setTime(this.startDate);
        return String.format("%02d:%02d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
    }

    public String getEndTime(){
        Calendar cal = Calendar.getInstance();
        cal.setTime(this.endDate);
        return String.format("%02d:%02d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
    }

    public String getDay(){
        return new SimpleDateFormat("dd MMMM yyyy").format(this.startDate);
    }

    public String timeRemaining() {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(this.startDate);
        return formattedTimeLeft(calendar.getTimeInMillis() - System.currentTimeMillis());
    }

    public int timeRemainingInSeconds() {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(this.startDate);
        return (int)TimeUnit.MILLISECONDS.toSeconds(calendar.getTimeInMillis() - System.currentTimeMillis());
    }

    public boolean sameLessonsAndSameDate(Lesson lesson) {
        if(lesson.getUID().equals(this.UID))
            if (lesson.getStartDate().equals(this.startDate))
                if(lesson.getEndDate().equals(this.endDate))
                    if(lesson.getName().equals(this.name))
                        return (lesson.getDescription().equals(this.description));

        return false;

    }

    public boolean isComingSoon(){

        boolean res = false;

        if(this.startDate.getTime() >= System.currentTimeMillis()){
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.setTime(this.startDate);
            int days = (int)(TimeUnit.MILLISECONDS.toDays(cal.getTimeInMillis()-System.currentTimeMillis()));
            if(days <= Integer.parseInt(Config.get("WATCH_UP")))
                res = true;
        }

        return res;
    }

    @Override
    public int compareTo(@NotNull Lesson aLesson) {
        Date aDate = aLesson.getStartDate();
        return this.startDate.compareTo(aDate);
    }

    @Override
    public boolean equals(Object lesson){
        if(lesson instanceof Lesson)
            return ((Lesson) lesson).getUID().equals(this.getUID());

        return false;
    }

    @Override
    public String toString() {
        return "Lesson[ name=" + this.name + ", startDate=" + this.startDate + ", endDate=" + this.endDate +
                ", description=" + this.endDate + ", nameProfessor=" + this.professor.getName() +
                ", room=" + this.room + " ]";
    }

    private static String formattedTimeLeft(final long millis) {
        final int days = (int)(TimeUnit.MILLISECONDS.toDays(millis));
        final int hrs = (int)(TimeUnit.MILLISECONDS.toHours(millis) % 24L);
        final int min = (int)(TimeUnit.MILLISECONDS.toMinutes(millis) % 60L);

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

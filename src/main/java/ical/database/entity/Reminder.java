package ical.database.entity;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @since 1.8
 */
public class Reminder extends Entity implements Comparable<Reminder>{

    private int id;

    private String name;

    private Date date;

    private Date created_at;

    private String author;

    private String recipient;

    private OGuild guild;

    public Reminder(){

    }

    public Reminder(String name, Date date, String author, String recipient, OGuild guild) {
        this.name = name;
        this.date = date;
        this.created_at = new Date();
        this.author = author;
        this.recipient = recipient;
        this.guild = guild;
    }


    public Reminder(int id, String name, Date date, Date created_at, String author, String recipient, OGuild guild) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.created_at = created_at;
        this.author = author;
        this.recipient = recipient;
        this.guild = guild;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public OGuild getGuild() {
        return guild;
    }

    public void setGuild(OGuild guild) {
        this.guild = guild;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    @Override
    public String toString() {

        return id+' '+name+' '+date+ ' '+created_at+' '+author+' '+' '+recipient+' '+guild;
    }

    @Override
    public int compareTo(@NotNull Reminder aReminder) {
        Date aDate = aReminder.getDate();
        if(this.date.compareTo(aDate) == 0)
            return this.getName().compareTo(aReminder.getName());
        else
            return this.date.compareTo(aDate);
    }

    public String getDay(){
        return new SimpleDateFormat("dd/MM/yyyy").format(this.date);
    }

    public String getFormattedDateForComparison(){
        return new SimpleDateFormat("yyyyMMdd").format(this.date);
    }

    public String getTime(){
        Calendar cal = Calendar.getInstance();
        cal.setTime(this.date);
        return String.format("%02d:%02d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
    }

    public int timeRemainingInSeconds() {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(this.date);
        return (int) TimeUnit.MILLISECONDS.toSeconds(calendar.getTimeInMillis() - System.currentTimeMillis());
    }

}
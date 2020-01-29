package ical.database.entity;

import ical.util.ModificationType;

import java.util.Date;

public class OEventChange {

    private String id;
    private Date date;
    private ModificationType type;


    public OEventChange(String id, Date date, ModificationType type) {
        this.id = id;
        this.date = date;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public ModificationType getType(){
        return type;
    }

    public void setType(ModificationType type){
        this.type = type;
    }




}

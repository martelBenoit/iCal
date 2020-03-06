package ical.database.entity;

import org.jetbrains.annotations.NotNull;

public class Room implements Comparable<Room> {

    private String name;

    private Lesson lesson = null;

    private boolean available;

    public Room(String name, Lesson lesson, boolean available) {
        this.name = name;
        this.lesson = lesson;
        this.available = available;
    }

    public Room(String name, boolean available) {
        this.name = name;
        this.available = available;
    }

    public String getUsualName(){
        return name.replaceAll("V-TO-ENSIbs\\s?-\\s?","");
    }

    public Lesson getLesson(){
        return this.lesson;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public void setLesson(Lesson lesson){
        this.lesson = lesson;
    }


    @Override
    public int compareTo(@NotNull Room o) {
        return isAvailable() == o.isAvailable() ? getUsualName().compareTo(o.getUsualName()) : this.isAvailable()?-1:o.isAvailable()?1:0;
    }
}

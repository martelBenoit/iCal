package ical.database.entity;

public class Room {

    private String name;

    private boolean available;

    public Room(String name, boolean available) {
        this.name = name;
        this.available = available;
    }

    public String getUsualName(){
        return name.replace("V-TO-ENSIbs-","");
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


}

package ical.database.entity;

import org.jetbrains.annotations.NotNull;

/**
 * Room class. Implements {@link Room}
 *
 * @author Benoît Martel
 * @version 1.0
 */
public class Room implements Comparable<Room> {

    /**
     * the room name.
     */
    private String name;

    /**
     * the lesson linked to this Room.
     */
    private Lesson lesson = null;

    /**
     * the room availability indicator.
     */
    private boolean available;

    /**
     * Default constructor.
     *
     * @param name the room name
     * @param available the room availability
     */
    public Room(String name, boolean available) {
        this.name = name;
        this.available = available;
    }

    /**
     * Get the room usual name.
     *
     * @return the room usal name
     */
    public String getUsualName(){

        String ret = name.replaceAll("V-TO-ENSIBS\\s?-\\s?","");
        return ret.replaceAll("V-TO-ENSIbs\\s?-\\s?","");
    }

    /**
     * Get the lesson linked to this room.
     *
     * @return the lesson linked to this room
     */
    public Lesson getLesson(){
        return this.lesson;
    }

    /**
     * Get the room name.
     *
     * @return the room name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the room name.
     *
     * @param name the room name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Check if this room is available.
     *
     * @return true if the room is available, false otherwise
     */
    public boolean isAvailable() {
        return available;
    }

    /**
     * Set the indicator of the room availability
     *
     * @param available true if the room is available, false otherwise
     */
    public void setAvailable(boolean available) {
        this.available = available;
    }

    /**
     * Set the lesson linked to this room.
     *
     * @param lesson the lesson linked to this room
     */
    public void setLesson(Lesson lesson){
        this.lesson = lesson;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(@NotNull Room o) {
        return isAvailable() == o.isAvailable() ? getUsualName().compareTo(o.getUsualName()) :
                this.isAvailable()?-1:o.isAvailable()?1:0;
    }
}

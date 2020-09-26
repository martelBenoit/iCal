package ical.util;

/**
 * ModificationType enum.
 *
 * <br>Manages the type of modification for a lesson.
 *
 * @author Benoît Martel
 * @version 1.0
 */
public enum ModificationType {

    ADD(1),
    REMOVE(2),
    MOVE(3);

    /**
     * the modification type value.
     */
    private final int value;

    /**
     * Constructor.
     *
     * @param value the value
     */
    ModificationType(int value) {
        this.value = value;
    }


    /**
     * Get the integer value of the actual ModificationType.
     *
     * @return the value
     */
    public int getValue() {
        return value;
    }

}

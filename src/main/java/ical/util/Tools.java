package ical.util;

import java.util.Date;
import java.util.Calendar;

/**
 * Static Tools class.
 * <br>This class offers different tools for managing schedules.
 *
 * @author Benoît Martel
 * @version 1.0
 */
public class Tools {

    /**
     * Retrieves the date resulting from the addition between today's date and the number of days passed in parameter.
     * @param nbDay the number of day to be added to today's date
     * @return the date resulting from the addition between today's date and the number of days passed in parameter
     */
    public static Date addDaysToTodayDate(int nbDay) {
        if(nbDay < 0)
            throw new NumberFormatException("nbDay must be superior or egal to zero !");
        else{
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, nbDay);
            return getDateWithTimeToZero(cal.getTime());
        }
    }

    /**
     * Resetting the date time.
     * @param date the date for which the time should be set to zero.
     * @return the new date without the time
     */
    public static Date getDateWithTimeToZero(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(java.util.Calendar.HOUR, 0);
        cal.set(java.util.Calendar.MINUTE, 0);
        cal.set(java.util.Calendar.SECOND, 0);
        cal.set(java.util.Calendar.MILLISECOND, 0);
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
        return cal.getTime();
    }

}
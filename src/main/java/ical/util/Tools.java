package ical.util;

import ical.database.entity.Lesson;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Date;
import java.util.Calendar;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Static Tools class.
 * <br>This class offers different tools for managing schedules.
 *
 * @author Benoît Martel
 * @version 1.0
 */
public class Tools {

    public static int colorPos = 0;

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

    /**
     * Check that the lesson is in the supervision zone.
     *
     * @param lesson the lesson to check
     * @return true if the lesson is in the supervision zone
     */
    public static boolean verifyWatchUp(@NotNull Lesson lesson){

        boolean res = false;

        if(lesson.getStartDate().getTime() >= System.currentTimeMillis()){
            Calendar cal = Calendar.getInstance();
            cal.setTime(lesson.getStartDate());

            Calendar actual = Calendar.getInstance();
            actual.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

            int days = (int)(TimeUnit.MILLISECONDS.toDays(cal.getTimeInMillis()-actual.getTimeInMillis()));
            if(days <= 14)
                res = true;
        }

        return res;
    }

    /**
     * Get a random nice color
     * @return a random nice color
     */
    public static Color getRandomColor(long seed, int multiple){

        return Color.decode(Colorize.get(seed, multiple));
    }


    public static long stringToSeed(String s) {
        if (s == null) {
            return 0;
        }
        long hash = 0;
        for (char c : s.toCharArray()) {
            hash = 31L*hash + c;
        }
        return hash;
    }



}
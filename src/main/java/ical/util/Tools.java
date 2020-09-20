package ical.util;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.Date;
import java.util.Calendar;
import java.util.Random;

/**
 * Static Tools class.
 * <br>This class offers different tools for managing schedules.
 *
 * @author Benoît Martel
 * @version 1.0
 */
public class Tools {

    private static final Logger LOGGER = LoggerFactory.getLogger(Tools.class);

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

    public static int getNumberFromAString(String number){

        if(number == null)
            return -1;

        int ret = -1;

        try{
            ret = Integer.parseInt(number);
        }catch (NumberFormatException e){
            LOGGER.error(number + "is not a number");
        }

        return ret;
    }

    public static Color getRandomColor(){

        //to get rainbow, pastel colors
        Random random = new Random();
        final float hue = random.nextFloat();
        final float saturation = 0.6f;//1.0 for brilliant, 0.0 for dull
        final float luminance = 0.8f; //1.0 for brighter, 0.0 for black
        return Color.getHSBColor(hue, saturation, luminance);
    }


}
package ical.core;

import ical.database.entity.Lesson;
import ical.util.Tools;
import org.junit.jupiter.api.Test;

import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.*;

class ScheduleTest {

    @Test
    void verifyWatchUp() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, 10);
        Lesson lesson = new Lesson("0","Test",calendar.getTime(),null,"description",null,null);
        assertTrue(Tools.verifyWatchUp(lesson));
    }
}
package ical.core;

import ical.database.entity.Lesson;
import org.junit.jupiter.api.Test;

import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.*;

class ScheduleTest {

    @Test
    void verifyWatchUp() {
        Schedule schedule = new Schedule();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, 10);
        Lesson lesson = new Lesson("0","Test",calendar.getTime(),null,"description",null,null);
        assertTrue(schedule.verifyWatchUp(lesson));
    }
}
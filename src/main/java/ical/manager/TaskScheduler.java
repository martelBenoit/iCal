package ical.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.*;

/**
 * Class TaskScheduler.<br>
 * Class used to generate tasks to be executed periodically.<br>
 * All the tasks created are stored in a Map.
 *
 * @author Benoît Martel
 * @version 1.0
 */
public class TaskScheduler {

    /**
     * the logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskScheduler.class);

    /**
     * the map of the tasks created
     */
    private final Map<String, ScheduledFuture> tasks;

    /**
     * Constructor.
     * <br> Initialize the HashMap.
     */
    public TaskScheduler(){
        tasks = new HashMap<>();
    }

    /**
     * Create a task that starts every 30 seconds.
     *
     * @param name the name of the task
     * @param runnable the runnable object to launch
     */
    public void run30seconds(String name, Runnable runnable){
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        LOGGER.info("Task '" + name + "' will next run 30 seconds.");
        ScheduledFuture future = scheduler.scheduleAtFixedRate(runnable, 0, 30, TimeUnit.SECONDS);
        tasks.put(name,future);
    }

    /**
     * Create a task that starts every minute.
     *
     * @param name the name of the task
     * @param runnable the runnable object to launch
     */
    public void runMinutely(String name, Runnable runnable){
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        LOGGER.info("Task '" + name + "' will next run every minute.");
        ScheduledFuture future = scheduler.scheduleAtFixedRate(runnable, 0, 1, TimeUnit.MINUTES);
        tasks.put(name,future);
    }


    /**
     * Create a task that starts every x minute(s).
     *
     * @param name the name of the task
     * @param runnable the runnable object to launch
     * @param period the period in minutes
     */
    public void runPeriod(String name, Runnable runnable, int period){

        if(period > 0 && period < 60){
            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

            LOGGER.info("Task '" + name + "' will next run "+period+" minute(s).");
            ScheduledFuture future = scheduler.scheduleAtFixedRate(runnable, 0, period, TimeUnit.MINUTES);
            tasks.put(name,future);
        }
        else
            LOGGER.error("Invalid period");
    }

    /**
     * Create a task that starts at midnight every day.
     *
     * @param name the name of the task
     * @param runnable the runnable object to launch
     */
    public void runAtMidnight(String name, Runnable runnable){

        long delayTime;
        final long initialDelay = LocalDateTime.now().until(
                LocalDate.now().plusDays(1).atTime(0, 2),
                ChronoUnit.MINUTES);

        if (initialDelay > TimeUnit.DAYS.toMinutes(1))
            delayTime = LocalDateTime.now().until(LocalDate.now().atTime(0, 1), ChronoUnit.MINUTES);
        else
            delayTime = initialDelay;


        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        LOGGER.info("Task '" + name + "' will next run at midnight");

        ScheduledFuture future = scheduler.scheduleAtFixedRate(
                runnable,
                delayTime,
                TimeUnit.DAYS.toMinutes(1),
                TimeUnit.MINUTES
        );

        tasks.put(name,future);

    }

    public void runAt8EveryMonday(String name, Runnable runnable){

        LocalDateTime dateNextRun =
                LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY)).atTime(8, 0,0);
        long delayTime = LocalDateTime.now().until(dateNextRun, ChronoUnit.SECONDS);

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        LOGGER.info("Task '" + name + "' will next run at "+dateNextRun+" in "+delayTime/60/60+" hour(s)");
        LOGGER.info("Second execution scheduled at : "
                + LocalDateTime.now().plusSeconds(delayTime+TimeUnit.DAYS.toSeconds(7))
        );

        ScheduledFuture future = scheduler.scheduleAtFixedRate(
                runnable,
                delayTime,
                TimeUnit.DAYS.toSeconds(7),
                TimeUnit.SECONDS
        );

        tasks.put(name,future);

    }

    /**
     * Create a task that starts only one time.
     *
     * @param name the name of the task
     * @param runnable the runnable object to launch
     */
    public void runOneTime(String name, Runnable runnable){
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        LOGGER.info("Task '" + name + "' starting");
        scheduler.execute(runnable);

    }

}
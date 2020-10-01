package ical.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Class TaskScheduler.<br>
 * Class used to generate tasks to be executed periodically.<br>
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
     * Executor service
     */
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(10);

    /**
     * Create a task that starts every 30 seconds.
     *
     * @param name     the name of the task
     * @param runnable the runnable object to launch
     */
    public void run30seconds(String name, Runnable runnable) {
        LOGGER.info("Task '" + name + "' will run every 30 seconds.");
        executorService.scheduleAtFixedRate(runnable, 0, 30000, TimeUnit.MILLISECONDS);
    }

    /**
     * Create a task that starts every minute.
     *
     * @param name     the name of the task
     * @param runnable the runnable object to launch
     */
    public void runMinutely(String name, Runnable runnable) {
        LocalDateTime dateNextRun = LocalDate.now().atTime(LocalDateTime.now().getHour(),LocalDateTime.now().getMinute(),0);
        dateNextRun = dateNextRun.plusMinutes(1);

        long delayTime = LocalDateTime.now().until(dateNextRun, ChronoUnit.MILLIS);
        LOGGER.info("Task '" + name + "' will run every minute.");
        executorService.scheduleAtFixedRate(runnable, delayTime, 60000, TimeUnit.MILLISECONDS);
    }


    /**
     * Create a task that starts every x minute(s).
     *
     * @param name     the name of the task
     * @param runnable the runnable object to launch
     * @param period   the period in minutes
     */
    public void runPeriod(String name, Runnable runnable, int period) {

        if (period > 0 && period < 60) {
            LOGGER.info("Task '" + name + "' will run every " + period + " minute(s).");
            executorService.scheduleAtFixedRate(runnable, 0, period, TimeUnit.MINUTES);
        } else
            LOGGER.error("Invalid period");
    }

    /**
     * Create a task that starts at midnight every day.
     *
     * @param name     the name of the task
     * @param runnable the runnable object to launch
     */
    public void runAtMidnight(String name, Runnable runnable) {

        long delayTime;
        final long initialDelay = LocalDateTime.now().until(
                LocalDate.now().plusDays(1).atTime(0, 2),
                ChronoUnit.MINUTES);

        if (initialDelay > TimeUnit.DAYS.toMinutes(1))
            delayTime = LocalDateTime.now().until(LocalDate.now().atTime(0, 1), ChronoUnit.MINUTES);
        else
            delayTime = initialDelay;



        LOGGER.info("Task '" + name + "' will run every day at midnight");


        executorService.scheduleAtFixedRate(runnable, delayTime, TimeUnit.DAYS.toMinutes(1), TimeUnit.MINUTES);
    }

    public void runAt8EveryMonday(String name, Runnable runnable) {


        LocalDateTime dateNextRun = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY)).atTime(8, 0, 0);
        long delayTime = LocalDateTime.now().until(dateNextRun, ChronoUnit.SECONDS);

        LOGGER.info("Task '" + name + "' will run at " + dateNextRun + " in " + delayTime / 60 / 60 + " hour(s)");
        LOGGER.info("Second execution scheduled at : " + LocalDateTime.now().plusSeconds(delayTime + TimeUnit.DAYS.toSeconds(7)));


        executorService.scheduleAtFixedRate(runnable, delayTime, TimeUnit.DAYS.toSeconds(7), TimeUnit.SECONDS);
    }

    /**
     * Create a task that starts only one time.
     *
     * @param name     the name of the task
     * @param runnable the runnable object to launch
     */
    public void runOneTime(String name, Runnable runnable) {
        LOGGER.info("Running '" + name);
        executorService.execute(runnable);
    }

    /**
     * Shutdown the executor and cancel any running task
     */
    public void shutdown(){
        executorService.shutdownNow();
    }

}
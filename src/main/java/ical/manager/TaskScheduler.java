package ical.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;

import java.util.concurrent.*;

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
    private static final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(20);


    /**
     * Create a task that starts every minute.
     *
     * <br>Example : 07:00:50, 07:01:50 (10 seconds before next minute)
     *
     * @param name     the name of the task
     * @param runnable the runnable object to launch
     */
    public static void runMinutelySpecial(String name, Runnable runnable) {
        LocalDateTime dateNextRun = LocalDate.now().atTime(LocalDateTime.now().getHour(),LocalDateTime.now().getMinute(),0);
        dateNextRun = dateNextRun.plusSeconds(50);

        long delayTime = LocalDateTime.now().until(dateNextRun, ChronoUnit.MILLIS);
        LOGGER.info("Task '" + name + "' will run every minute.");
        executorService.scheduleAtFixedRate(runnable, delayTime, 60000, TimeUnit.MILLISECONDS);
    }

    /**
     * Create a task that starts every minute.
     *
     * @param name     the name of the task
     * @param runnable the runnable object to launch
     */
    public static void runMinutely(String name, Runnable runnable) {
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
    public static void runPeriod(String name, Runnable runnable, int period) {

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
    public static void runAtMidnight(String name, Runnable runnable) {

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

    public static void runAt8H5MEveryMonday(String name, Runnable runnable) {

        LocalDateTime dateNextRun = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY)).atTime(8, 5, 0);
        long delayTime = LocalDateTime.now().until(dateNextRun, ChronoUnit.SECONDS);

        LOGGER.info("Task '" + name + "' will run at " + dateNextRun + " in " + delayTime / 60 / 60 + " hour(s)");
        LOGGER.info("Second execution scheduled at : " + LocalDateTime.now().plusSeconds(delayTime + TimeUnit.DAYS.toSeconds(7)));
        
        executorService.scheduleAtFixedRate(runnable, delayTime, TimeUnit.DAYS.toSeconds(7), TimeUnit.SECONDS);
    }


    public static String info(){
        StringBuilder ret = new StringBuilder();
        try{
            if (executorService instanceof ScheduledThreadPoolExecutor) {
                ScheduledThreadPoolExecutor implementation = (ScheduledThreadPoolExecutor) executorService;
                ret.append("Nombre de thread actif         :\t").append(implementation.getActiveCount()).append("\n");
                ret.append("Nombre d'élément dans la queue :\t").append(implementation.getQueue().size()).append("\n");
                ret.append("Contenu de la queue            :\n");

                for(Object task : implementation.getQueue()){

                    Field callableField = task.getClass().getSuperclass().getDeclaredField("callable");
                    callableField.setAccessible(true);
                    Object callable = callableField.get(task);

                    Field runnableField = callable.getClass().getDeclaredField("task");
                    runnableField.setAccessible(true);
                    Object runnable = runnableField.get(callable);

                    ret.append("\t").append(runnable.getClass().getCanonicalName()).append("\n");
                }

            }
        }catch(NoSuchFieldException | IllegalAccessException exception){
            LOGGER.error(exception.getMessage(),exception.fillInStackTrace());
        }

        return ret.toString();
    }

}
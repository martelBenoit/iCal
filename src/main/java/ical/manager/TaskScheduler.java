package ical.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

public class TaskScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskScheduler.class);

    private final Map<String, ScheduledFuture> tasks;

    public TaskScheduler(){
        tasks = new HashMap<>();
    }

    public void runMinutely(String name, Runnable runnable){
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        LOGGER.info("Task '" + name + "' will next run every minute.");
        ScheduledFuture future = scheduler.scheduleAtFixedRate(runnable, 0, 1, TimeUnit.MINUTES);
        tasks.put(name,future);
    }


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

    public void runAtMidnight(String name, Runnable runnable){

        long delayTime;
        final long initialDelay = LocalDateTime.now().until(LocalDate.now().plusDays(1).atTime(0, 1), ChronoUnit.MINUTES);

        if (initialDelay > TimeUnit.DAYS.toMinutes(1)) {
            delayTime = LocalDateTime.now().until(LocalDate.now().atTime(0, 1), ChronoUnit.MINUTES);
        } else {
            delayTime = initialDelay;
        }

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        LOGGER.info("Task '" + name + "' will next run at midnight");

        ScheduledFuture future = scheduler.scheduleAtFixedRate(runnable, delayTime, TimeUnit.DAYS.toMinutes(1), TimeUnit.MINUTES);


        tasks.put(name,future);

    }

    public void runOneTime(String name, Runnable runnable){
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        LOGGER.info("Task '" + name + "' starting");
        scheduler.execute(runnable);



    }



}

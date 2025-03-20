package com.example.schedulingtasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
public class ScheduledTasks {

    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Scheduled(fixedRate = 5000)
    public void reportCurrentTime() {
        log.info("The time is now {}", dateFormat.format(new Date()));
    }

    // New scheduled task with cron expression
    // This example runs at 6:45 PM every day - adjust the time as needed
//    @Scheduled(cron = "0 45 18 * * ?")
      @Scheduled(cron = "0 * * * * ?")
    public void processNamesInParallel() {
        log.info("Starting parallel name processing at {}", dateFormat.format(new Date()));

        List<String> names = Arrays.asList(
                "Alice", "Bob", "Charlie", "David", "Emma",
                "Frank", "Grace", "Henry", "Ivy", "Jack",
                "Karen", "Liam", "Mia", "Noah", "Olivia",
                "Paul", "Quinn", "Ryan", "Sophia", "Thomas"
        );

        // Split the list into two batches
        List<String> firstBatch = names.subList(0, names.size() / 2);
        List<String> secondBatch = names.subList(names.size() / 2, names.size());

        // Create ExecutorService with 2 threads
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        // Submit first batch to thread 1
        executorService.submit(() -> {
            for (String name : firstBatch) {
                log.info("Thread 1 processing: {} at {}",
                        name, dateFormat.format(new Date()));
                try {
                    Thread.sleep(500); // Slow down to see the interleaving
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        // Submit second batch to thread 2
        executorService.submit(() -> {
            for (String name : secondBatch) {
                log.info("Thread 2 processing: {} at {}",
                        name, dateFormat.format(new Date()));
                try {
                    Thread.sleep(500); // Slow down to see the interleaving
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        // Shutdown executor and wait for completion
        executorService.shutdown();
        try {
            // Wait for both threads to finish
            if (executorService.awaitTermination(1, TimeUnit.MINUTES)) {
                log.info("All done here!");
            } else {
                log.warn("Timeout occurred while waiting for tasks to complete");
            }
        } catch (InterruptedException e) {
            log.error("Interrupted while waiting for tasks to complete", e);
            Thread.currentThread().interrupt();
        }
    }
}
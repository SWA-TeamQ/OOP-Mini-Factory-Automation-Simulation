package org.Automation.Controllers.Simluators;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Simple SimulationClock used by the repo.
 * Advances internal simTime in small steps and notifies observers every simulated second.
 */
public class SimulationClock {

    public interface ClockObservers {
        void onTick(LocalDateTime currentTime);
    }

    private static SimulationClock instance;

    private LocalDateTime simTime;
    private LocalDateTime lastNotificationDate;
    private int speedFactor = 1;
    private volatile boolean isPaused = true;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final int Tick_Per_MS = 50; // internal small step

    private final ArrayList<ClockObservers> observers = new ArrayList<>();

    private SimulationClock() {
        this.simTime = LocalDateTime.now();
        this.lastNotificationDate = this.simTime;
        startIntegralTimer();
    }

    // Singleton accessor
    public static synchronized SimulationClock getInstance() {
        if (instance == null) {
            instance = new SimulationClock();
        }
        return instance;
    }

    private void startIntegralTimer() {
        scheduler.scheduleAtFixedRate(() -> {
            if (!isPaused) tick();
        }, 0, Tick_Per_MS, TimeUnit.MILLISECONDS);
    }

    private void tick() {
        long millisToAdd = Tick_Per_MS * (long) speedFactor;
        simTime = simTime.plusNanos(millisToAdd * 1_000_000L);

        long secondsElapsed = ChronoUnit.SECONDS.between(lastNotificationDate, simTime);
        if (secondsElapsed >= 1) {
            notifyObservers();
            lastNotificationDate = lastNotificationDate.plusSeconds(secondsElapsed);
        }
    }

    public synchronized void register(ClockObservers observer) {
        if (observer != null && !observers.contains(observer)) observers.add(observer);
    }

    public synchronized void unregister(ClockObservers observer) {
        observers.remove(observer);
    }

    private synchronized void notifyObservers() {
        for (ClockObservers o : observers) {
            try {
                o.onTick(simTime);
            } catch (Exception ex) {
                System.err.println("[SimulationClock] observer error: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    /** Unpause and allow ticks to advance and notify. */
    public void start() {
        isPaused = false;
        System.out.println("[SimulationClock] started at " + simTime);
    }

    /** Pause notifications (clock stops advancing notifications). */
    public void stop() {
        isPaused = true;
        System.out.println("[SimulationClock] stopped at " + simTime);
    }

    public synchronized void setSpeedFactor(int speed) {
        if (speed <= 0) speed = 1;
        this.speedFactor = speed;
        System.out.println("[SimulationClock] speedFactor set to " + speedFactor);
    }

    public LocalDateTime getCurrentTime() {
        return simTime;
    }

    public boolean isRunning() {
        return !isPaused;
    }
}

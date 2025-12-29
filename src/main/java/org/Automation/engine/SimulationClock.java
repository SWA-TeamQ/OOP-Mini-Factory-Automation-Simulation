package org.Automation.engine;

import org.Automation.core.Tickable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Central Simulation Clock.
 * Drives all time-dependent components in the system.
 */
public class SimulationClock {
    private static SimulationClock instance;

    private LocalDateTime currentSecond = LocalDateTime.now();
    private long logicalTick = 0;
    private boolean paused = true;
    private int speedFactor = 1;

    // The interval between logical ticks in real-time milliseconds
    private final int TICK_INTERVAL_MS = 50;

    private final List<ClockObserver> observers = new CopyOnWriteArrayList<>();
    private final List<Tickable> tickables = new CopyOnWriteArrayList<>();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    private SimulationClock() {
        scheduler.scheduleAtFixedRate(this::tick, 1000, TICK_INTERVAL_MS, TimeUnit.MILLISECONDS);
    }

    public static synchronized SimulationClock getInstance() {
        if (instance == null) {
            instance = new SimulationClock();
        }
        return instance;
    }

    private void tick() {
        if (paused) return;

        // Advance logical time
        logicalTick++;
        
        // Advance simulated wall-clock time
        // Each tick represents TICK_INTERVAL_MS * speedFactor of simulated time
        currentSecond = currentSecond.plusNanos((long) TICK_INTERVAL_MS * speedFactor * 1_000_000L);

        // Notify Tickable components (Physical simulation)
        for (Tickable tickable : tickables) {
            tickable.tick(logicalTick);
        }

        // Notify Observers (UI / Logging)
        for (ClockObserver observer : observers) {
            observer.onTick(currentSecond);
        }
    }

    public long getLogicalTick() {
        return logicalTick;
    }

    public LocalDateTime getCurrentTime() {
        return currentSecond;
    }

    public void registerObserver(ClockObserver observer) {
        observers.add(observer);
    }

    public void registerTickable(Tickable tickable) {
        tickables.add(tickable);
    }

    public void start() {
        paused = false;
        System.out.println("The Simulation has been started at time: \n" + currentSecond);
    }

    public void stop() {
        paused = true;
        System.out.println("The Simulation has been stopped at time: \n" + currentSecond);
    }

    public void setSpeedFactor(int speed) {
        this.speedFactor = speed;
        System.out.println("The Simulation speed has been updated to: " + speedFactor);
    }

    public boolean isPaused() {
        return paused;
    }

    public void shutdown() {
        scheduler.shutdown();
    }
}
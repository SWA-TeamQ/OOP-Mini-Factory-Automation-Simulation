package org.automation.engine;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * SimulationClock
 *
 * - Singleton clock used by sensors
 * - Notifies registered ClockObserver instances once per simulated second
 * - Exposes getTickIntervalMs() for sensors to adapt increments
 */
public class SimulationClock {

    // Singleton instance
    private static SimulationClock instance;

    // Simulation time state
    private LocalDateTime simTime;
    private LocalDateTime lastNotificationDate;

    // Speed and pause control
    private volatile int speedFactor = 1;
    private volatile boolean isPaused = false;

    // Scheduler and tick configuration
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "SimulationClock");
        t.setDaemon(true);
        return t;
    });
    private static final int TICK_PER_MS = 50; // base tick in milliseconds

    // Observers (thread-safe for iteration)
    private final CopyOnWriteArrayList<ClockObserver> observers = new CopyOnWriteArrayList<>();

    private SimulationClock() {
        this.simTime = LocalDateTime.now();
        this.lastNotificationDate = this.simTime;
        startIntegralTimer();
    }

    public static synchronized SimulationClock getInstance() {
        if (instance == null) {
            instance = new SimulationClock();
        }
        return instance;
    }

    private void startIntegralTimer() {
        scheduler.scheduleAtFixedRate(() -> {
            if (!isPaused) {
                tick();
            }
        }, 0, TICK_PER_MS, TimeUnit.MILLISECONDS);
    }

    private void tick() {
        long millisToAdd = (long) TICK_PER_MS * speedFactor;
        simTime = simTime.plusNanos(millisToAdd * 1_000_000L);

        long secondsAfterPreviousNotification = ChronoUnit.SECONDS.between(lastNotificationDate, simTime);
        if (secondsAfterPreviousNotification >= 1) {
            notifyObservers();
            lastNotificationDate = lastNotificationDate.plusSeconds(secondsAfterPreviousNotification);
        }
    }

    public synchronized void register(ClockObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public synchronized void unregister(ClockObserver observer) {
        observers.remove(observer);
    }

    private void notifyObservers() {
        for (ClockObserver observer : observers) {
            try {
                observer.onTick(simTime);
            } catch (Throwable t) {
                // keep clock running even if an observer throws
                t.printStackTrace();
            }
        }
    }

    public synchronized void start() {
        isPaused = false;
        System.out.println("The Simulation has been started at time:\n" + simTime);
    }

    public synchronized void stop() {
        isPaused = true;
        System.out.println("The Simulation has been stopped at time:\n" + simTime);
    }

    public void setSpeedFactor(int speed) {
        if (speed <= 0) throw new IllegalArgumentException("Speed must be > 0");
        this.speedFactor = speed;
        System.out.println("Simulation speed set to: " + speedFactor);
    }

    public LocalDateTime getCurrentTime() {
        return simTime;
    }

    /**
     * Method expected by sensors: returns effective tick interval in milliseconds.
     */
    public int getTickIntervalMs() {
        return TICK_PER_MS * speedFactor;
    }

    /**
     * Graceful shutdown for the scheduler (call at application exit).
     */
    public synchronized void shutdown() {
        try {
            // request orderly shutdown
            scheduler.shutdown();
            // wait briefly for tasks to finish
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                // force shutdown if not finished
                scheduler.shutdownNow();
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    System.err.println("SimulationClock did not terminate");
                }
            }
        } catch (InterruptedException ie) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}

package org.Automation.controllers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * SimulationClock - Concrete class for controlling timing of simulations
 */
public class SimulationClock {
    private static SimulationClock instance;
    private Timer timer;
    private boolean isRunning = false;
    private int currentTime;
    private int intervalMs;
    private String clockName;
    private List<ClockObserver> observers = new ArrayList<>();

    // Observer interface
    public interface ClockObserver {
        void onTick(LocalDateTime currentTime);
    }

    public static SimulationClock getInstance() {
        if (instance == null) {
            instance = new SimulationClock("GlobalClock", 1000);
        }
        return instance;
    }

    public SimulationClock(String clockName, int intervalMs) {
        this.clockName = clockName;
        this.intervalMs = intervalMs;
    }

    public void register(ClockObserver observer) {
        observers.add(observer);
    }

    public void unregister(ClockObserver observer) {
        observers.remove(observer);
    }

    /**
     * Start the simulation clock
     */
    public void start() {
        if (isRunning) return;
        
        isRunning = true;
        currentTime = 0; // Reset to 0
        timer = new Timer(clockName, true);
        
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                tick();
                notifyObservers();
            }
        }, 0, intervalMs);
    }

    private void notifyObservers() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        for (ClockObserver observer : observers) {
            observer.onTick(currentDateTime);
        }
    }

    /**
     * Stop the simulation clock
     */
    public void stop() {
        if (!isRunning) return;
        
        isRunning = false;
        if (timer != null) {
            timer.cancel();
        }
    }

    /**
     * Check if the simulation is currently running
     */
    public boolean isRunning() {
        return isRunning;
    }

    /**
     * Get the current tick count
     */
    public int getCurrentTime() {
        return currentTime;
    }

    /**
     * Get the current interval in milliseconds
     */
    public int getIntervalMs() {
        return intervalMs;
    }

    /**
     * Set a new interval for the simulation
     */
    public void setInterval(int intervalMs) {
        this.intervalMs = intervalMs;
        if (isRunning) {
            stop();
            start();
        }
    }

    /**
     * Override this method in subclasses to define tick behavior
     */
    protected void tick() {
        currentTime++;
    }
}









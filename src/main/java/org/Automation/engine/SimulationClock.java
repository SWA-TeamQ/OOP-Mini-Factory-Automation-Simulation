package org.Automation.engine;

import java.util.ArrayList;
import java.util.List;

public class SimulationClock {
    private long currentTime = 0;
    private long tickIntervalMillis = 1000;
    private boolean running = false;
    private final List<ClockObserver> observers = new ArrayList<>();

    public void registerObserver(ClockObserver observer) {
        observers.add(observer);
    }

    public void start() {
        running = true;
        new Thread(() -> {
            while (running) {
                try {
                    Thread.sleep(tickIntervalMillis);
                    currentTime++;
                    for (ClockObserver observer : observers) {
                        observer.tick(currentTime);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();
    }

    public void stop() {
        running = false;
    }

    public long getCurrentTime() {
        return currentTime;
    }

    public void setTickIntervalMillis(long tickIntervalMillis) {
        this.tickIntervalMillis = tickIntervalMillis;
    }
}

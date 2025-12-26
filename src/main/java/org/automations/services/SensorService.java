package org.automations.services;

import java.time.LocalDateTime;

import org.automations.core.EventBus;
import org.automations.entities.Sensor;

/**
 * Minimal SensorService scaffold implementing ISensorService.
 * Responsible for updating sensors each tick and publishing events via
 * EventBus.
 */
public class SensorService implements ISensorService {

    private volatile boolean running = false;
    private String status = "stopped";
    private final EventBus eventBus = EventBus.getInstance();

    @Override
    public void onTick(LocalDateTime time) {
        if (!running)
            return;
        // TODO: iterate sensors, read values, publish events
        System.out.println("[SensorService] tick at " + time + " — updating sensors.");
        // example publish (actual sensor iteration to be added)
        // eventBus.publish(new SensorEvent(...));
    }

    @Override
    public void start() {
        running = true;
        status = "running";
        System.out.println("[SensorService] started.");
    }

    @Override
    public void stop() {
        running = false;
        status = "stopped";
        System.out.println("[SensorService] stopped.");
    }

    @Override
    public String getStatus() {
        return status;
    }

    // helper to register/configure sensors (to be expanded)
    public void registerSensor(Sensor sensor) {
        // TODO: keep registry of sensors
        System.out.println("[SensorService] registered sensor: " + sensor.getId());
    }
}

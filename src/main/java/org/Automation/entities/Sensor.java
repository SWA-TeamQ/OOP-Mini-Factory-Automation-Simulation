package org.Automation.entities;

import org.Automation.core.*;
import org.Automation.engine.*;
import org.Automation.engine.Tickable;
import org.Automation.events.*;
import java.util.Random;

/**
 * Represents a physical sensor monitoring the production line.
 * Samples values based on the SimulationClock (Time-Driven).
 */
public class Sensor implements Tickable {
    private final String id;
    private final String locationId; // The station this sensor monitors (decoupled)
    private final String type; // e.g., "Temperature", "Weight"
    private final EventBus eventBus;
    private final Random random = new Random();

    private double threshold;
    private int samplingRateTicks; // Sample every X ticks
    private double lastValue;

    public Sensor(String id, String locationId, String type, double threshold, int samplingRateTicks,
            EventBus eventBus) {
        this.id = id;
        this.locationId = locationId;
        this.type = type;
        this.threshold = threshold;
        this.samplingRateTicks = samplingRateTicks;
        this.eventBus = eventBus;

        // Register with the central clock
        SimulationClock.getInstance().registerTickable(this);
    }

    public String getId() {
        return id;
    }

    public String getLocationId() {
        return locationId;
    }

    public String getType() {
        return type;
    }

    @Override
    public void tick(long currentTick) {
        // Only sample at the configured rate
        if (currentTick % samplingRateTicks == 0) {
            sample();
        }
    }

    private void sample() {
        // Simulate a measurement (e.g., base value + noise)
        lastValue = 20.0 + random.nextDouble() * 10.0;

        eventBus.publish(new SensorEvent("measurement_taken",
                "Sensor " + id + " [" + type + "] measured: " + String.format("%.2f", lastValue)));

        if (lastValue > threshold) {
            eventBus.publish(new SensorEvent("threshold_exceeded",
                    "ALARM: Sensor " + id + " [" + type + "] exceeded threshold! Value: "
                            + String.format("%.2f", lastValue)));
        }
    }

    public double getLastValue() {
        return lastValue;
    }

    public double getThreshold() {
        return threshold;
    }

    public int getSamplingRateTicks() {
        return samplingRateTicks;
    }
}

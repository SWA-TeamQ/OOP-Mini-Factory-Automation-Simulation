package org.Automation.entities;

import org.Automation.core.*;
import org.Automation.engine.*;
import org.Automation.engine.*;
import org.Automation.events.*;

import java.util.Random;

/**
 * Represents a physical sensor monitoring the production line.
 * Samples values based on the SimulationClock (Time-Driven).
 */
public abstract class Sensor implements Tickable {
    protected final String id;
    protected final String locationId;
    protected final String type;
    protected final EventBus eventBus;
    protected final Random random = new Random();

    protected double threshold;
    protected int samplingRateTicks;
    protected double lastValue;

    public Sensor(String id, String locationId, String type, double threshold, int samplingRateTicks,
            EventBus eventBus) {
        this.id = id;
        this.locationId = locationId;
        this.type = type;
        this.threshold = threshold;
        this.samplingRateTicks = samplingRateTicks;
        this.eventBus = eventBus;

        SimulationClock.getInstance().registerTickable(this);
        subscribeToEvents();
    }

    // Abstract methods defining the sensor contract (from old implementation)
    public abstract void readValue();

    public abstract void updateValue(Object value);

    public abstract void validateReading();

    public abstract String getSensorInfo();

    protected void subscribeToEvents() {
        // Sensors should listen for general sensor-related commands if applicable
        // Concrete classes will override or extend this logic.
    }

    @Override
    public void tick(long currentTick) {
        if (currentTick % samplingRateTicks == 0) {
            readValue();
            validateReading();
        }
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

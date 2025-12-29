package org.Automation.entities;

import org.Automation.core.EventBus;
import org.Automation.engine.SimulationClock;
import org.Automation.engine.Tickable;

import java.util.Random;

/**
 * Represents a physical sensor monitoring the production line.
 * Polls every tick during processing stage.
 * Only raises events when threshold is exceeded.
 */
public abstract class Sensor implements Tickable {
    protected final String id;
    protected final String locationId;
    protected final String type;
    protected final EventBus eventBus;
    protected final Random random = new Random();

    protected double threshold;
    protected double lastValue;

    // The product currently being measured (set during processing)
    protected String currentProductId;

    public Sensor(String id, String locationId, String type, double threshold, EventBus eventBus) {
        this.id = id;
        this.locationId = locationId;
        this.type = type;
        this.threshold = threshold;
        this.eventBus = eventBus;
        this.currentProductId = null;

        SimulationClock.getInstance().registerTickable(this);
    }

    /**
     * Sets the current product being measured.
     * Called when a product starts processing at this sensor's location.
     */
    public void setCurrentProduct(String productId) {
        this.currentProductId = productId;
    }

    /**
     * Clears the current product (processing finished).
     */
    public void clearCurrentProduct() {
        this.currentProductId = null;
    }

    /**
     * Samples and validates every tick if a product is being processed.
     */
    @Override
    public void tick(long currentTick) {
        if (currentProductId != null) {
            readValue();
            validateReading();
        }
    }

    // Abstract methods to be implemented by specific sensor types
    public abstract void readValue();

    public abstract void validateReading();

    public abstract String getSensorInfo();

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

    public String getCurrentProductId() {
        return currentProductId;
    }
}

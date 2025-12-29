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

    // Logging limit: max 5 logs per product at this sensor's location
    private static final int MAX_LOGS_PER_PRODUCT = 5;
    private int logCount = 0;

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
     * Resets log counter for the new product.
     */
    public void setCurrentProduct(String productId) {
        this.currentProductId = productId;
        this.logCount = 0; // Reset log counter for new product
    }

    /**
     * Clears the current product (processing finished).
     */
    public void clearCurrentProduct() {
        this.currentProductId = null;
        this.logCount = 0; // Reset log counter
    }

    /**
     * Samples and validates every tick if a product is being processed.
     * Limits console logging to MAX_LOGS_PER_PRODUCT entries.
     */
    @Override
    public void tick(long currentTick) {
        if (currentProductId != null) {
            readValue();

            // Real-time sensor display during processing (limited to 5 logs)
            if (logCount < MAX_LOGS_PER_PRODUCT) {
                logCount++;
                org.Automation.core.Logger
                        .info(String.format("[Tick %d] [Sensor %s] Product %s | %s: %.2f (Threshold: %.2f) [Log %d/%d]",
                                currentTick, id, currentProductId, type, lastValue, threshold, logCount,
                                MAX_LOGS_PER_PRODUCT));
            }

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

    public int getSamplingRateTicks() {
        return 1;
    }
}

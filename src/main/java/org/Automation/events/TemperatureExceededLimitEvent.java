package org.automation.events;

import org.automation.entities.ProductItem;
import org.automation.events.abstracts.ProductSensorEvent;

/**
 * Raised when temperature measurement exceeds the threshold.
 */
public class TemperatureExceededLimitEvent extends ProductSensorEvent {
    private final double measuredValue;
    private final double threshold;

    public TemperatureExceededLimitEvent(ProductItem productItem, double measuredValue, double threshold) {
        super("TemperatureExceededLimitEvent", productItem);
        this.measuredValue = measuredValue;
        this.threshold = threshold;
    }

    public double getMeasuredValue() {
        return measuredValue;
    }

    public double getThreshold() {
        return threshold;
    }

    @Override
    public String toString() {
        return "Product " + getProductId() + " exceeded temperature limit: " +
                "measured " + measuredValue + ", threshold " + threshold;
    }
}

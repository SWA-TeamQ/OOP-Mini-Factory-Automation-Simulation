package org.automation.events;

import org.automation.entities.ProductItem;
import org.automation.events.abstracts.ProductSensorEvent;

/**
 * Raised when weight measurement exceeds the threshold.
 */
public class WeightExceededLimitEvent extends ProductSensorEvent {

    private final double measuredValue;
    private final double threshold;

    public WeightExceededLimitEvent(ProductItem productItem, double measuredValue, double threshold) {
        super("WeightExceededLimitEvent", productItem);
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
        return "Product " + getProductId() + " exceeded weight limit: " +
                "measured " + measuredValue + ", threshold " + threshold;
    }
}

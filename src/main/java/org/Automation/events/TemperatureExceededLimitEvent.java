package org.Automation.events;

/**
 * Raised when temperature measurement exceeds the threshold.
 */
public class TemperatureExceededLimitEvent extends Event {
    private final String productId;
    private final double measuredValue;
    private final double threshold;

    public TemperatureExceededLimitEvent(String productId, double measuredValue, double threshold) {
        super("TemperatureExceededLimitEvent");
        this.productId = productId;
        this.measuredValue = measuredValue;
        this.threshold = threshold;
    }

    public String getProductId() {
        return productId;
    }

    public double getMeasuredValue() {
        return measuredValue;
    }

    public double getThreshold() {
        return threshold;
    }
}

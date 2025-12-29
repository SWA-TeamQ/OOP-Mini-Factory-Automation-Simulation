package org.Automation.events;

/**
 * Raised when weight measurement exceeds the threshold.
 */
public class WeightExceededLimitEvent extends Event {
    private final String productId;
    private final double measuredValue;
    private final double threshold;

    public WeightExceededLimitEvent(String productId, double measuredValue, double threshold) {
        super("WeightExceededLimitEvent");
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

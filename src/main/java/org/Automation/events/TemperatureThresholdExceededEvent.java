package org.Automation.events;

public class TemperatureThresholdExceededEvent extends Event {
    private final String sensorId;
    private final double value;
    private final double threshold;

    public TemperatureThresholdExceededEvent(String sensorId, double value, double threshold) {
        super("TemperatureThresholdExceededEvent", sensorId);
        this.sensorId = sensorId;
        this.value = value;
        this.threshold = threshold;
    }

    public String getSensorId() {
        return sensorId;
    }

    public double getValue() {
        return value;
    }

    public double getThreshold() {
        return threshold;
    }
}

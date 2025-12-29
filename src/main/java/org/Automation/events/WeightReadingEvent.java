package org.Automation.events;

public class WeightReadingEvent extends Event {
    private final String sensorId;
    private final double value;
    private final String formattedValue;

    public WeightReadingEvent(String sensorId, double value, String formattedValue) {
        super("WeightReadingEvent", sensorId);
        this.sensorId = sensorId;
        this.value = value;
        this.formattedValue = formattedValue;
    }

    public String getSensorId() {
        return sensorId;
    }

    public double getValue() {
        return value;
    }

    public String getFormattedValue() {
        return formattedValue;
    }
}

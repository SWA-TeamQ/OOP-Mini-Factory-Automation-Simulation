package org.Automation.entities;

import org.Automation.core.*;
import org.Automation.events.*;

public class TemperatureSensor extends Sensor {

    public TemperatureSensor(String id, String locationId, double threshold, int samplingRateTicks, EventBus eventBus) {
        super(id, locationId, "Temperature", threshold, samplingRateTicks, eventBus);
    }

    @Override
    protected void subscribeToEvents() {
        super.subscribeToEvents();
        eventBus.subscribe("SET_TEMPERATURE", new EventSubscriber() {
            @Override
            public void onEvent(Event event) {
                // Assuming event source is the value or contains it.
                // For simplicity in this student project, we assume payload is parsable or is
                // the val.
                if (event.getSource() instanceof Double) {
                    updateValue(event.getSource());
                } else if (event.getSource() instanceof String) {
                    try {
                        updateValue(Double.parseDouble((String) event.getSource()));
                    } catch (NumberFormatException e) {
                        // Ignore invalid format
                    }
                }
            }
        });
    }

    @Override
    public void readValue() {
        // Simulate reading (ambient 20C + random variation 5C)
        lastValue = 20.0 + random.nextDouble() * 5.0;
        eventBus.publish(new TemperatureReadingEvent(id, lastValue,
                "Temperature Sensor " + id + " measured: " + String.format("%.2f C", lastValue)));
    }

    @Override
    public void updateValue(Object value) {
        if (value instanceof Double) {
            this.lastValue = (Double) value;
        }
    }

    @Override
    public void validateReading() {
        if (lastValue > threshold) {
            eventBus.publish(new TemperatureThresholdExceededEvent(id, lastValue, threshold));
        }
    }

    @Override
    public String getSensorInfo() {
        return "TemperatureSensor[" + id + "] Type=Temperature Threshold=" + threshold;
    }
}

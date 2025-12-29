package org.Automation.entities;

import org.Automation.core.*;
import org.Automation.events.*;

public class WeightSensor extends Sensor {

    public WeightSensor(String id, String locationId, double threshold, int samplingRateTicks, EventBus eventBus) {
        super(id, locationId, "Weight", threshold, samplingRateTicks, eventBus);
    }

    @Override
    protected void subscribeToEvents() {
        super.subscribeToEvents();
        eventBus.subscribe("SET_WEIGHT", new EventSubscriber() {
            @Override
            public void onEvent(Event event) {
                if (event.getSource() instanceof Double) {
                    updateValue(event.getSource());
                } else if (event.getSource() instanceof String) {
                    try {
                        updateValue(Double.parseDouble((String) event.getSource()));
                    } catch (NumberFormatException e) {
                        // Ignore
                    }
                }
            }
        });
    }

    @Override
    public void readValue() {
        // Simulate reading (nominal 10kg + random variation 2kg)
        lastValue = 10.0 + random.nextDouble() * 2.0;
        eventBus.publish(new WeightReadingEvent(id, lastValue,
                "Weight Sensor " + id + " measured: " + String.format("%.2f kg", lastValue)));
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
            eventBus.publish(new WeightThresholdExceededEvent(id, lastValue, threshold));
        }
    }

    @Override
    public String getSensorInfo() {
        return "WeightSensor[" + id + "] Type=Weight Threshold=" + threshold;
    }
}

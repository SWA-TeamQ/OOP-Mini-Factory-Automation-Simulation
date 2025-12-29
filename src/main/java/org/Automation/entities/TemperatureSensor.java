package org.automation.entities;

import org.automation.core.*;
import org.automation.events.*;

/**
 * Temperature sensor that monitors product temperature.
 * Raises TemperatureExceededLimitEvent only when threshold is exceeded.
 */
public class TemperatureSensor extends Sensor {

    public TemperatureSensor(String id, String locationId, double threshold, EventBus eventBus) {
        super(id, locationId, "Temperature", threshold, eventBus);
    }

    @Override
    public void readValue() {
        // Simulate reading (ambient 20C + random variation 5C)
        lastValue = 20.0 + random.nextDouble() * 5.0;
    }

    @Override
    public void validateReading() {
        if (lastValue > threshold && currentProductId != null) {
            eventBus.publish(new TemperatureExceededLimitEvent(currentProductId, lastValue, threshold));
            Logger.warn("Temperature exceeded: " + String.format("%.2f C", lastValue) +
                    " > " + threshold + " for product " + currentProductId);
        }
    }

    @Override
    public String getSensorInfo() {
        return "TemperatureSensor[" + id + "] Type=Temperature Threshold=" + threshold;
    }
}

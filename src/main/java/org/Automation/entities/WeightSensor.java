package org.Automation.entities;

import org.Automation.core.*;
import org.Automation.events.*;

/**
 * Weight sensor that monitors product weight.
 * Raises WeightExceededLimitEvent only when threshold is exceeded.
 */
public class WeightSensor extends Sensor {

    public WeightSensor(String id, String locationId, double threshold, EventBus eventBus) {
        super(id, locationId, "Weight", threshold, eventBus);
    }

    @Override
    public void readValue() {
        // Simulate reading (nominal 10kg + random variation 2kg)
        lastValue = 10.0 + random.nextDouble() * 2.0;
    }

    @Override
    public void validateReading() {
        if (lastValue > threshold && currentProductId != null) {
            eventBus.publish(new WeightExceededLimitEvent(currentProductId, lastValue, threshold));
            Logger.warn("Weight exceeded: " + String.format("%.2f kg", lastValue) +
                    " > " + threshold + " for product " + currentProductId);
        }
    }

    @Override
    public String getSensorInfo() {
        return "WeightSensor[" + id + "] Type=Weight Threshold=" + threshold;
    }
}

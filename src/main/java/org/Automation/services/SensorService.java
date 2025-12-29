package org.automation.services;

import org.automation.core.*;
import org.automation.entities.*;
import org.automation.repositories.*;

public class SensorService implements ISensorService {

    private final SensorRepository sensorRepo;

    public SensorService(SensorRepository sensorRepo, EventBus eventBus) {
        this.sensorRepo = sensorRepo;
        // eventBus unused
    }

    @Override
    public void readSensor(Sensor sensor) {
        sensorRepo.save(sensor);
        // eventBus.publish(new SensorEvent("sensor_read", sensor)); // REMOVED
        Logger.info("Sensor " + sensor.getId() + " read.");
    }
}

package org.Automation.services;

import org.Automation.entities.*;
import org.Automation.repositories.*;
import org.Automation.core.*;
import org.Automation.events.*;

public class SensorService implements ISensorService {

    private final SensorRepository sensorRepo;
    private final EventBus eventBus;

    public SensorService(SensorRepository sensorRepo, EventBus eventBus) {
        this.sensorRepo = sensorRepo;
        this.eventBus = eventBus;
    }

    @Override
    public void readSensor(Sensor sensor) {
        sensorRepo.save(sensor);
        eventBus.publish(new SensorEvent("sensor_read", sensor));
    }
}

package org.Automation.services;

import org.Automation.entities.Sensor;
import org.Automation.repositories.SensorRepository;
import org.Automation.core.EventBus;

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
        eventBus.publish("sensor_read", sensor);
    }
}

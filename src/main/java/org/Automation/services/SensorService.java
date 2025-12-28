package org.Automation.services;

import org.Automation.entities.Sensor;
import org.Automation.repositories.SensorRepository;
import org.Automation.core.EntityFactory;
import org.Automation.core.EventBus;

public class SensorService implements ISensorService {

    private final SensorRepository sensorRepo;
    private final EventBus eventBus;

    public SensorService(SensorRepository sensorRepo, EventBus eventBus) {
        this.sensorRepo = sensorRepo;
        this.eventBus = eventBus;
    }

    // Read a sensor and simulate a value
    public double readSensor(String sensorId) {
        Sensor sensor = sensorRepo.findById(sensorId);
        if (sensor == null) return 0.0;

        // simulate a reading (random for demo)
        double value = switch (sensor.getType()) {
            case "Temperature Sensor" -> 20 + Math.random() * 10; // 20-30°C
            case "Weight Sensor" -> 0.5 + Math.random() * 2.0;    // 0.5-2.5kg
            default -> 0.0;
        };
        sensor.setCurrentValue(value);

        // save & publish event
        sensorRepo.save(sensor);
        eventBus.publish("sensor_read", sensor);
        return value;
    }

    public Sensor getSensor(String id) {
        return sensorRepo.findById(id);
    }

   public Sensor createSensor(String id, String type) {
    Sensor sensor = EntityFactory.createSensor(id, type);
    sensorRepo.save(sensor); // now it's saved safely
    return sensor;
}


    @Override
    public void readSensor(Sensor sensor) {
        sensorRepo.save(sensor);
        eventBus.publish("sensor_read", sensor);
    }
}

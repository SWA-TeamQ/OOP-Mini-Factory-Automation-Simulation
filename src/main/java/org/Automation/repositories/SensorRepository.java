package org.Automation.repositories;

import org.Automation.entities.Sensor;

public class SensorRepository extends Repository<Sensor> {

    @Override
    public void save(Sensor sensor) { add(sensor.getId(), sensor); }

    @Override
    public void delete(String id) { remove(id); }
}

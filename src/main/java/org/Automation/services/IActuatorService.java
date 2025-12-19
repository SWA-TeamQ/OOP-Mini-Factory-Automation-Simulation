package org.automation.services;
import org.automation.entities.Actuator;

import java.util.List;

public interface IActuatorService {
    void onTick(double currentSecond);
    void register(Actuator actuator);
    void activate(int id);
    void deactivate(int id);
    List<Actuator> getActuators();
}

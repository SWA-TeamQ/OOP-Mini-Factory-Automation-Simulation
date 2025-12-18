package org.automation.services;

import java.time.LocalDateTime;
import java.util.List;
import org.automation.entities.Actuator;

/**
 * Public actuator service contract used by controllers.
 * Keep minimal surface required by WorkflowController and other callers.
 */
public interface IActuatorService {
    void onTick(LocalDateTime time);
    void listActuators();
    void registerActuator(Actuator actuator);
    void activate(String id);
    void deactivate(String id);
    List<Actuator> getActuators();
}

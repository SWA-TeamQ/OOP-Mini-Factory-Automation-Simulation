package org.Automation.services;

import org.Automation.entities.Machine;

public interface IActuatorService {
    void activateActuator(String machineId);
    void deactivateActuator(String machineId);
    void startMachine(Machine machine);
    void stopMachine(Machine machine);
}

package org.Automation.services;

import org.Automation.entities.Actuator;
import org.Automation.entities.Machine;
import org.Automation.repositories.MachineRepository;
import org.Automation.core.EventBus;

public class ActuatorService implements IActuatorService {

    private final MachineRepository machineRepo;
    private final EventBus eventBus;

    public ActuatorService(MachineRepository machineRepo, EventBus eventBus) {
        this.machineRepo = machineRepo;
        this.eventBus = eventBus;
    }

    @Override
    public void activateActuator(String machineId) {
        Machine machine = machineRepo.findById(machineId);
        if (machine != null) {
            Actuator actuator = machine.getActuator();
            if (actuator != null) {
                actuator.activate();
                eventBus.publish("actuator_activated", actuator);
            }
        }
    }

    @Override
    public void deactivateActuator(String machineId) {
        Machine machine = machineRepo.findById(machineId);
        if (machine != null) {
            Actuator actuator = machine.getActuator();
            if (actuator != null) {
                actuator.deactivate();
                eventBus.publish("actuator_deactivated", actuator);
            }
        }
    }

    @Override
    public void startMachine(Machine machine) {
        machine.start();
        machineRepo.save(machine);
        eventBus.publish("machine_started", machine);
    }

    @Override
    public void stopMachine(Machine machine) {
        machine.stop();
        machineRepo.save(machine);
        eventBus.publish("machine_stopped", machine);
    }
}
